use std::collections::HashMap;
use std::{env, io};
use std::sync::Arc;
use dashmap::DashMap;
use dotenv::dotenv;
use futures_util::future::try_join_all;
use qdrant_client::{Payload, Qdrant, QdrantError};
use qdrant_client::qdrant::{SearchPoints, WithPayloadSelector, CreateCollectionBuilder, Distance, VectorParamsBuilder, VectorsConfig, VectorParamsMap, PointId, Vector, PointStruct, UpsertPointsBuilder};
use qdrant_client::qdrant::point_id::PointIdOptions;
use qdrant_client::qdrant::vectors_config::Config;
use qdrant_client::qdrant::with_payload_selector::SelectorOptions;
use serde_json::{json, Value};
use crate::dto::crawler_dto::CrawlResultsData;
use crate::dto::model_dto::QueryType;
use crate::utils::vectorizer::vectorize_crawled_results;

pub async fn search_indexed_results(query_vectors: &Vec<f32>) -> Result<Vec<Value>, QdrantError> {
    // Compare and Search past Indexed Results
    dotenv().ok();
    let api_key = env::var("QDRANT_DB_API_KEY").unwrap_or_else(|_| "".to_string());

    let endpoint = "https://a70ce690-ffe4-4363-8db8-5dad057ae856.europe-west3-0.gcp.cloud.qdrant.io:6334";

    let client = Qdrant::from_url(endpoint)
        .api_key(api_key)
        .skip_compatibility_check()
        .build()?;

    let common_collection = "common_index".to_string();

    let indexed_results_response = client
        .search_points(
            SearchPoints {
                collection_name: common_collection,
                vector: query_vectors.clone(),
                vector_name: Some("summary".to_string()),
                limit: 5,
                score_threshold: Some(0.6666666667),
                with_payload: Some(WithPayloadSelector {
                    selector_options: Some(SelectorOptions::Enable(true)),
                }),
                ..Default::default()
            }
        )
        .await?;

    let indexed_results: Vec<Value> = indexed_results_response
        .result
        .iter()
        .map(|point| {
            let id = match point.id.as_ref().and_then(|id| id.point_id_options.as_ref()) {
                Some(PointIdOptions::Num(n)) => json!(n),
                Some(PointIdOptions::Uuid(s)) => json!(s),
                None => Value::Null,
            };

            json!({
            "id": id,
            "score": point.score,
            "payload": point.payload
        })
        })
        .collect();

    Ok(indexed_results)
}


pub async fn process_crawled_results_to_index(
    query: String,
    crawl_results: CrawlResultsData,
    queries_state: Arc<DashMap<String, Vec<f32>>>
) {
    // If Crawl Results Data is not None
    if let Some(crawl_data) = &crawl_results.data {
        let search_results_summaries: Vec<&str> = crawl_data.detailed_search_results.iter()
            .map(|result| result.summary.as_str()) 
            .collect();
        // Vectorize Search Results
        let results_vectors_res = vectorize_crawled_results(search_results_summaries).await;
        if let Ok(results_vectors) = results_vectors_res {
            // Store vectorized results along with metadata to Indexes
            let query_types = &crawl_results.crawl_metadata.query_type;
            let categorised_indexes: Vec<String> = query_types
                .iter()
                .map(|q_type| match q_type {
                    QueryType::ENTITY => "entity_index".to_string(),
                    QueryType::EVENT => "event_index".to_string(),
                    QueryType::REGIONAL => "regional_index".to_string(),
                    QueryType::INFORMATIONAL => "informational_index".to_string(),
                })
                .collect();
            let mut query_vectors: Vec<f32> = vec![];
            // Get query state and clear it from App State
            if let Some((query, vectors)) = queries_state.remove(&query){
                query_vectors = vectors;
                let index_db_res = store_results_to_indexes(
                    &query_vectors,
                    &results_vectors,
                    crawl_results,
                    categorised_indexes
                ).await;

                match index_db_res {
                    Ok(response) => {
                        println!("{:?}", response);
                    }
                    Err(e) => {
                        println!("FAILED TO INSERT TO INDEXES:\n{:?}", e);
                    }
                }
            }
        }
    }
    else {
        println!("no crawl_results returned");
    }
}


pub async fn store_results_to_indexes(
    query_vectors: &Vec<f32>,
    results_vectors: &Vec<Vec<f32>>,
    crawl_results: CrawlResultsData,
    categorised_indexes: Vec<String>
) -> Result<String, QdrantError> {
    // Store vectorized results along with metadata
    dotenv().ok();
    let api_key = env::var("QDRANT_DB_API_KEY").unwrap_or_else(|_| "".to_string());

    let endpoint = "https://a70ce690-ffe4-4363-8db8-5dad057ae856.europe-west3-0.gcp.cloud.qdrant.io:6334";

    let client = Qdrant::from_url(endpoint)
        .api_key(api_key)
        .skip_compatibility_check()
        .build()?;

    let common_collection = "common_index".to_string();

    let overall_answer = &crawl_results.data.as_ref().unwrap().overall_answer_and_summary;
    let facts = &crawl_results.data.as_ref().unwrap().facts;
    let fact_relationships = &crawl_results.data.as_ref().unwrap().fact_relationships;
    let crawl_metadata = crawl_results.crawl_metadata;

    let common_index_points: Vec<PointStruct> = crawl_results.data.as_ref().unwrap().detailed_search_results
        .iter()
        .zip(results_vectors.into_iter())
        .enumerate()
        .map(|(i, (result_source, summary_vec))| {
            let point_id = PointId::from(i as u64);

            let payload: Payload = json!({
                "results_metadata": {
                    "overall_answer_and_summary": overall_answer,
                    "facts": facts,
                    "fact_relationships": fact_relationships
                },
                "source_metadata": result_source,
                "crawl_metadata": crawl_metadata,
            }).try_into().unwrap();

            PointStruct::new(
                point_id,
                HashMap::from([
                    (
                        "query".to_string(),
                        Vector::from(query_vectors.clone())
                    ),
                    (
                        "summary".to_string(),
                        Vector::from(summary_vec.clone()),
                    ),
                ]),
                payload
            )
        })
        .collect();


    let other_indexes_points: Vec<PointStruct> = crawl_results.data.as_ref().unwrap().detailed_search_results
        .iter()
        .zip(results_vectors.into_iter())
        .enumerate()
        .map(|(i, (result_source, summary_vec))| {
            let point_id = PointId::from(i as u64);

            let payload: Payload = json!({
                "results_metadata": {
                    "overall_answer_and_summary": overall_answer,
                    "facts": facts,
                    "fact_relationships": fact_relationships
                },
                "source_metadata": result_source,
                "crawl_metadata": crawl_metadata,
            }).try_into().unwrap();

            PointStruct::new(
                point_id,
                Vector::from(summary_vec.clone()),
                payload
            )
        })
        .collect();

    // Store in Common Index
    client.upsert_points(
        UpsertPointsBuilder::new(
            common_collection,
            common_index_points
        )
    ).await?;

    let tasks: Vec<_> = categorised_indexes
        .into_iter()
        .map(|index_name| {
            let client_clone = client.clone();
            let points = other_indexes_points.clone();

            tokio::spawn(async move {
                client_clone
                    .upsert_points(
                        UpsertPointsBuilder::new(index_name, points)
                    )
                    .await
            })
        })
        .collect();

    let results = try_join_all(tasks)
        .await
        .map_err(|e| {
            QdrantError::from(io::Error::new(io::ErrorKind::Other, e.to_string()))
        })?;

    for res in results {
        res?;
    }

    Ok("<<<<CRAWL RESULTS INSERTED TO INDEX DB>>>>>>".to_string())
}


