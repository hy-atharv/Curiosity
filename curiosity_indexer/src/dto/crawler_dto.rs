use serde::{Deserialize, Serialize};
use serde_json::Value;
use crate::dto::model_dto::StructuredQueryExpandedData;

#[derive(Serialize, Deserialize)]
pub struct CrawlRequestData {
    pub query: String,
    pub enriched_query: String,
    pub expanded_query_data: StructuredQueryExpandedData
}

#[derive(Serialize, Deserialize, Clone, Debug)]
pub struct CrawlResultsData {
    pub count: usize,
    pub data: Option<StructuredAgenticCrawlData>,
    pub crawl_metadata: StructuredQueryExpandedData
}

#[derive(Serialize, Deserialize, Clone)]
pub enum CrawlState {
    NOT_INITIATED,
    IN_PROCESS,
    FINISHED(CrawlResultsData),
    QUERY_EXPANSION_FAILED(String),
    CRAWL_FAILED(String),
}

#[derive(Serialize, Deserialize, Clone, Debug)]
pub struct StructuredAgenticCrawlData {
    pub overall_answer_and_summary: String,
    pub detailed_search_results: Vec<DetailedSearchResult>,
    pub facts: Vec<Fact>,
    pub fact_relationships: Vec<FactRelationshipEdge>
}

#[derive(Serialize, Deserialize, Clone, Debug)]
pub struct DetailedSearchResult {
    pub url: String,
    pub title: String,
    pub summary: String,
    pub date: String,
    pub credibility_reason: String,
}

#[derive(Serialize, Deserialize, Clone, Debug)]
pub struct Fact {
    pub claim: String,
    pub evidence_urls: Vec<String>,
}

#[derive(Serialize, Deserialize, Clone, Debug)]
pub struct FactRelationshipEdge {
    pub source_claim: String,
    pub target_claim: String,
    pub connection_type: FactRelationshipType,
}

#[derive(Serialize, Deserialize, Clone, Debug)]
pub enum FactRelationshipType {
    SUPPORTS,
    CONTRADICTS,
    ELABORATES,
    CAUSES
}