use std::sync::Arc;
use actix_web::{get, http, web, HttpResponse, Responder};
use actix_web::web::Bytes;
use futures_util::stream;
use crate::AppState;
use crate::dto::crawler_dto::CrawlState;
use crate::dto::status_types::StatusTypes;
use crate::dto::user_dto::{CrawledResultsResponseData, ReceivedUserQueryData};
use crate::utils::indexer_db::{process_crawled_results_to_index};

#[get("/crawlResults")]
async fn crawl_results(
    received_query_data: web::Query<ReceivedUserQueryData>,
    state: web::Data<AppState>
) -> impl Responder {
    println!("crawl_results received_query_data {:?}", &received_query_data);
    let query = &received_query_data.query;
    let query_clone = query.clone();
    let state_clone = state.clone();

    let crawl_state_events_receiver = state.crawl_state_events_tx.subscribe();

    let initial_seed = (crawl_state_events_receiver, query_clone, state_clone);

    let sse_stream = stream::unfold(initial_seed, |(mut rx, query, state)| async move {
        match rx.recv().await {
            Ok(state_msg) => {
                let response_msg = match state_msg {
                    CrawlState::FINISHED(crawl_results) => {
                        // Cache Results with metadata to Index DB
                        tokio::spawn({
                            let query_clone = query.clone();
                            let queries_map_state = Arc::clone(&state.queries_state);
                            let crawl_data = crawl_results.clone();
                            async move {
                                process_crawled_results_to_index(query_clone ,crawl_data, queries_map_state).await;
                            }
                        });
                        // Return Crawled Results to User
                        CrawledResultsResponseData{
                            count: crawl_results.count,
                            crawled_results: crawl_results.data,
                            status_type: StatusTypes::CRAWLING_FINISHED,
                            status_message: "Crawled Results Successfully".to_string(),
                        }
                    }
                    CrawlState::IN_PROCESS => {
                        CrawledResultsResponseData{
                            count: 0,
                            crawled_results: None,
                            status_type: StatusTypes::CRAWLING_IN_PROCESS,
                            status_message: "Crawling in process...".to_string(),
                        }
                    }
                    CrawlState::QUERY_EXPANSION_FAILED(error_message) => {
                        CrawledResultsResponseData{
                            count: 0,
                            crawled_results: None,
                            status_type: StatusTypes::GOOGLE_API_ERROR,
                            status_message: error_message,
                        }
                    }
                    CrawlState::CRAWL_FAILED(error_message) => {
                        CrawledResultsResponseData{
                            count: 0,
                            crawled_results: None,
                            status_type: StatusTypes::CRAWLING_FAILED,
                            status_message: error_message,
                        }
                    }
                    CrawlState::NOT_INITIATED => {
                        CrawledResultsResponseData{
                            count: 0,
                            crawled_results: None,
                            status_type: StatusTypes::NO_CRAWL_JOB_FOUND,
                            status_message: "No crawl job has been initiated for this query".to_string()
                        }
                    }
                };

                let json_data = serde_json::to_string(&response_msg).unwrap_or_else(|_| "{}".into());
                let payload = format!("data: {}\n\n", json_data);
                Some((Ok::<Bytes, actix_web::Error>(Bytes::from(payload)), (rx, query, state)))
            }
            Err(_) => None
        }
    });

    HttpResponse::Ok()
        .insert_header((http::header::CONTENT_TYPE, "text/event-stream"))
        .insert_header(("Cache-Control", "no-cache"))
        .insert_header(("Connection", "keep-alive"))
        .streaming(sse_stream)
}