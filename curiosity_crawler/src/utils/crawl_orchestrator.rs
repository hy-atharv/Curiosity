use actix_web::web;
use crate::AppState;
use crate::dto::request_dto::CrawlRequestData;
use crate::dto::response_dto::CrawlResultsData;
use crate::utils::common_crawl::{enumerate_cc_indexes};
use crate::utils::scraper::initiate_recursive_crawl_algorithm;

pub async fn dispatch_crawl(
    request_data: CrawlRequestData,
    state_data: web::Data<AppState>
) -> CrawlResultsData {

    // <========= QUERY DATA AND WEB CLIENT INIT ============>

    let query = request_data.query;

    let enriched_query = request_data.enriched_query;

    let expanded_query_data = request_data.expanded_query_data;

    let client = &state_data.web_client;


    // <============= COMMON CRAWL INDEX ENUMERATION ================>

    let seed_sources_res = enumerate_cc_indexes(
         &expanded_query_data.seed_urls,
         &expanded_query_data.from_date,
         &expanded_query_data.to_date,
         client
    ).await;


    // <================== DEPTH BASED CRAWL ALGORITHM =====================>

    if let Ok(seed_sources) = seed_sources_res {
        let recursive_crawl_result = initiate_recursive_crawl_algorithm(
            seed_sources,
            &query,
            &enriched_query,
            &expanded_query_data,
        ).await;
        
        if let Some(crawl_data) = recursive_crawl_result {
            return crawl_data
        }
    }
    
    CrawlResultsData{
        count: 0,
        data: None,
        crawl_metadata: expanded_query_data
    }
}



