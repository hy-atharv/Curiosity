use serde::{Deserialize, Serialize};
use crate::models::query_types::QueryType;


#[derive(Serialize, Deserialize, Clone)]
pub struct CrawlRequestData {
    pub query: String,
    pub enriched_query: String,
    pub expanded_query_data: StructuredQueryExpandedData,
}

#[derive(Serialize, Deserialize, Clone)]
pub struct StructuredQueryExpandedData{
    pub topics: Vec<String>,
    pub query_type: Vec<QueryType>,
    pub seed_urls: Vec<String>,
    pub content_keywords: Vec<String>,
    pub from_date: String,
    pub to_date: String,
}