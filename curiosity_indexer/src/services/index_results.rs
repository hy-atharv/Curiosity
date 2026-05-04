use actix_web::{post, web, HttpResponse, Responder};
use crate::{dto::{user_dto::ReceivedUserQueryData, status_types::StatusTypes}, utils::{query_processor::enrich_query, vectorizer::vectorize_query, indexer_db::search_indexed_results}, AppState};
use crate::dto::user_dto::IndexedResultsResponseData;
use crate::utils::crawler::initiate_crawl;

#[post("/indexResults")]
async fn index_results(
    received_query_data: web::Json<ReceivedUserQueryData>,
    state: web::Data<AppState>
) -> impl Responder {
    let query_data = received_query_data.into_inner();
    let query = query_data.query;
    let country = query_data.country;

    let state_data = state.clone();

    // Enrich Query
    let enriched_query_response = enrich_query(&query, &country).await;

    match enriched_query_response {
        Ok(enriched_query) => {
            // Initiate Crawling
            tokio::spawn({
                let query = query.clone();
                let enriched_query = enriched_query.clone();
                async move {
                    initiate_crawl(&query, &enriched_query, state_data).await;
                }
            });
            // Vectorize Query
            let query_vectors_response = vectorize_query(&query, &enriched_query).await;
            match query_vectors_response {
                Ok(query_vectors) => {
                    let indexed_results_response = search_indexed_results(&query_vectors).await;
                    // Caching query vectors for later DB indexing
                    state.queries_state.insert(
                        query,
                        query_vectors
                    );
                    match indexed_results_response {
                        Ok(indexed_results) => {
                            match indexed_results.len(){
                                0 => {
                                    HttpResponse::Ok().json(
                                        IndexedResultsResponseData{
                                            count: 0,
                                            indexed_results,
                                            status_type: StatusTypes::UNMATCHED_INDEXED_RESULTS,
                                            status_message: "No Matched Results Found".to_string(),
                                        }
                                    )
                                }
                                nonzero => {
                                    HttpResponse::Ok().json(
                                        IndexedResultsResponseData{
                                            count: indexed_results.len() as i8,
                                            indexed_results,
                                            status_type: StatusTypes::MATCHED_INDEXED_RESULTS,
                                            status_message: "Matched Results Found".to_string(),
                                        }
                                    )
                                }
                            }
                        }
                        Err(error) => {
                            HttpResponse::InternalServerError().json(
                                IndexedResultsResponseData{
                                    count: 0,
                                    indexed_results: vec![],
                                    status_type: StatusTypes::INDEX_DB_ERROR,
                                    status_message: error.to_string()
                                }
                            )
                        }
                    }
                }
                Err(error) => {
                    HttpResponse::InternalServerError().json(
                        IndexedResultsResponseData{
                            count: 0,
                            indexed_results: vec![],
                            status_type: StatusTypes::GOOGLE_API_ERROR,
                            status_message: error.to_string()
                        }
                    )
                }
            }
        }
        Err(error) => {
            HttpResponse::InternalServerError().json(
                IndexedResultsResponseData{
                    count: 0,
                    indexed_results: vec![],
                    status_type: StatusTypes::GOOGLE_API_ERROR,
                    status_message: error.to_string()
                }
            )
        }
    }
}