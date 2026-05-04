use serde::{Serialize, Deserialize};


#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct StructuredQueryExpandedData{
    pub topics: Vec<String>,
    pub query_type: Vec<QueryType>,
    pub seed_urls: Vec<String>,
    pub content_keywords: Vec<String>,
    pub from_date: String,
    pub to_date: String,
}

#[derive(Serialize, Deserialize, Debug, Clone)]
pub enum QueryType {
    EVENT,
    REGIONAL,
    INFORMATIONAL,
    ENTITY
}