use dotenv::dotenv;
use std::env;
use reqwest::{Client, Error};
use serde_json::{json, Value};
use crate::dto::crawler_dto::CrawlResultsData;

pub async fn vectorize_query(query: &str, enriched_query: &str) -> Result<Vec<f32>, Error> {
    // Vectorize User Query and Enriched Query for semantic Query
    dotenv().ok();
    let api_key = env::var("GEMINI_API_KEY").unwrap_or_else(|_| "".to_string());

    let endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-embedding-001:embedContent";

    let client = Client::new();

    let body = json!({
        "content": {
            "parts": [
                {
                    "text": query
                },
                {
                    "text": enriched_query
                }
            ]
        },
        "output_dimensionality": 768,
        "taskType": "RETRIEVAL_QUERY"
    });

    let res = client
        .post(endpoint)
        .header("x-goog-api-key", api_key)
        .json(&body)
        .send()
        .await?
        .json::<Value>()
        .await?;

    let query_vectors: Vec<f32> = res
        .get("embedding")
        .and_then(|e| e.get("values"))
        .and_then(|v| v.as_array())
        .map(|arr| {
            arr.iter()
                .filter_map(|n| n.as_f64())
                .map(|n| n as f32)
                .collect()
        })
        .unwrap_or_else(Vec::new);

    Ok(query_vectors)
}


pub async fn vectorize_crawled_results(crawl_results_summaries: Vec<&str>) -> Result<Vec<Vec<f32>>, Error> {
    // Vectorize Crawled Results Summaries for semantic Retrieval
    dotenv().ok();
    let api_key = env::var("GEMINI_API_KEY").unwrap_or_else(|_| "".to_string());

    let endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-embedding-001:batchEmbedContents";

    let client = Client::new();

    let requests: Vec<serde_json::Value> = crawl_results_summaries
        .iter()
        .map(|summary| {
            json!({
            "model": "models/gemini-embedding-001",
            "taskType": "RETRIEVAL_DOCUMENT",
            "output_dimensionality": 768,
            "content": {
                "parts": [{"text": summary}]
            }
        })
        })
        .collect();

    let body = json!({
        "requests": requests
    });

    let res = client
        .post(endpoint)
        .header("x-goog-api-key", api_key)
        .json(&body)
        .send()
        .await?
        .json::<Value>()
        .await?;

    let results_summaries_vectors: Vec<Vec<f32>> = res
        .get("embeddings")
        .and_then(|e| e.as_array())
        .map(|list| {
            list.iter()
                .filter_map(|obj| {
                    obj.get("values")
                        .and_then(|v| v.as_array())
                        .map(|vec_vals| {
                            vec_vals.iter()
                                .filter_map(|n| n.as_f64())
                                .map(|n| n as f32)
                                .collect::<Vec<f32>>()
                        })
                })
                .collect()
        })
        .unwrap_or_else(Vec::new);

    Ok(results_summaries_vectors)
}