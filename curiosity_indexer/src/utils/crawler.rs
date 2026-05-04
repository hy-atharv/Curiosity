use actix_web::web;
use reqwest::Client;
use crate::AppState;
use crate::dto::crawler_dto::{CrawlRequestData, CrawlResultsData, CrawlState};
use crate::utils::query_processor::expand_query;

pub async fn initiate_crawl(
    query: &str,
    enriched_query: &str,
    state: web::Data<AppState>
) {
    let crawl_state_events_handle = state.crawl_state_events_tx.send(
        CrawlState::IN_PROCESS
    );
    if let Err(e) = crawl_state_events_handle {
        println!("Failed to send crawl state events: {}", e.to_string());
    }
    else {
        print!(">>Crawl State Event Sent>>")
    }

    let expanded_query_data_response = expand_query(query, enriched_query).await;

    match expanded_query_data_response {
        Ok(some_data) => {
            match some_data {
                Some(expanded_data) => {
                    // Initiate Crawling
                    let crawl_request_data = CrawlRequestData{
                        query: query.to_string(),
                        enriched_query: enriched_query.to_string(),
                        expanded_query_data: expanded_data,
                    };

                    let endpoint = "http://127.0.0.1:3000/results";

                    let client = Client::new();

                    let response = client
                        .get(endpoint)
                        .json(&crawl_request_data)
                        .send()
                        .await;

                    println!("\n\n\n{:?}", response);

                    match response {
                        Ok(res) => {
                            let crawl_results_data_response = res.json::<CrawlResultsData>().await;

                            match crawl_results_data_response {
                                Ok(crawl_results_data) => {
                                    let crawl_state_events_handle = state.crawl_state_events_tx.send(
                                        CrawlState::FINISHED(crawl_results_data)
                                    );
                                    if let Err(e) = crawl_state_events_handle {
                                        println!("Failed to send crawl state events: {}", e.to_string());
                                    }
                                    else {
                                        print!(">>Crawl State Event Sent>>")
                                    }
                                }
                                Err(e) => {
                                    let crawl_state_events_handle = state.crawl_state_events_tx.send(
                                        CrawlState::CRAWL_FAILED(e.to_string())
                                    );
                                    if let Err(e) = crawl_state_events_handle {
                                        println!("Failed to send crawl state events: {}", e.to_string());
                                    }
                                    else {
                                        print!(">>Crawl State Event Sent>>")
                                    }
                                }
                            }
                        }
                        Err(e) => {
                            let crawl_state_events_handle = state.crawl_state_events_tx.send(
                                CrawlState::CRAWL_FAILED(e.to_string())
                            );
                            if let Err(e) = crawl_state_events_handle {
                                println!("Failed to send crawl state events: {}", e.to_string());
                            }
                            else {
                                print!(">>Crawl State Event Sent>>")
                            }
                        }
                    }
                }
                None => {
                    let crawl_state_events_handle = state.crawl_state_events_tx.send(
                        CrawlState::QUERY_EXPANSION_FAILED("No Expanded Query Data".to_string())
                    );
                    if let Err(e) = crawl_state_events_handle {
                        println!("Failed to send crawl state events: {}", e.to_string());
                    }
                    else {
                        print!(">>Crawl State Event Sent>>")
                    }
                }
            }
        }
        Err(e) => {
            let crawl_state_events_handle = state.crawl_state_events_tx.send(
                CrawlState::QUERY_EXPANSION_FAILED(e.to_string())
            );
            if let Err(e) = crawl_state_events_handle {
                println!("Failed to send crawl state events: {}", e.to_string());
            }
            else {
                print!(">>Crawl State Event Sent>>")
            }
        }
    }
}