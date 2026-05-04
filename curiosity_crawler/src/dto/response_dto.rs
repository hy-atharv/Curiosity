use serde::{Deserialize, Serialize};
use crate::dto::model_dto::StructuredAgenticCrawlData;
use crate::dto::request_dto::StructuredQueryExpandedData;

#[derive(Serialize, Deserialize, Clone)]
pub struct CrawlResultsData {
    pub count: usize,
    pub data: Option<StructuredAgenticCrawlData>, 
    pub crawl_metadata: StructuredQueryExpandedData
}