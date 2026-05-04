use serde::{Serialize, Deserialize};
use serde_json::Value;
use crate::dto::crawler_dto::{CrawlResultsData, CrawlState, StructuredAgenticCrawlData};
use crate::dto::status_types::StatusTypes;

#[derive(Debug, Serialize, Deserialize)]
pub struct ReceivedUserQueryData {
    pub query: String,
    pub country: String
}

#[derive(Serialize, Deserialize)]
pub struct IndexedResultsResponseData {
    pub count: i8,
    pub indexed_results: Vec<Value>,
    pub status_type: StatusTypes,
    pub status_message: String
}

#[derive(Serialize, Deserialize)]
pub struct CrawledResultsResponseData {
    pub count: usize,
    pub crawled_results: Option<StructuredAgenticCrawlData>,
    pub status_type: StatusTypes,
    pub status_message: String
}

// #[derive(Debug, Serialize, Deserialize)]
// pub struct EnrichedUserQueryData {
//     pub query: String,
//     pub country: String,
//     pub enriched_query: String,
//     pub query_vectors: Vec<f32>,
//     pub indexed_results_sources: Vec<String>
// }
