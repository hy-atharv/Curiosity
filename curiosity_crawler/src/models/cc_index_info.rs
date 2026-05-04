use chrono::NaiveDateTime;
use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Clone)]
pub struct CCIndexInfo {
    pub id: String,
    pub name: String,
    pub timegate: String,
    #[serde(rename = "cdx-api")]
    pub cdx_api: String,
    pub from: NaiveDateTime,
    pub to: NaiveDateTime,
}

#[derive(Serialize, Deserialize, Clone)]
pub struct CCIndexRecord {
    pub urlkey: String,
    pub timestamp: String,
    pub url: String,
    pub mime: String,
    #[serde(rename = "mime-detected")]
    pub mime_detected: String,
    pub status: String,
    pub digest: String,
    pub length: String,
    pub offset: String,
    pub filename: String
}