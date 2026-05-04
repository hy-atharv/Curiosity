use serde::{Deserialize, Serialize};
#[derive(Serialize, Deserialize)]
pub enum StatusTypes{
    GOOGLE_API_ERROR,
    INDEX_DB_ERROR,
    UNMATCHED_INDEXED_RESULTS,
    MATCHED_INDEXED_RESULTS,
    CRAWLING_IN_PROCESS,
    CRAWLING_FINISHED,
    CRAWLING_FAILED,
    NO_CRAWL_JOB_FOUND
}