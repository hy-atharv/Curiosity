use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Clone)]
pub struct PageContent {
    pub title: String,
    pub metadata: String,
    pub headers: String,
    pub content: String,
    pub links: Vec<String>
}

#[derive(Serialize, Deserialize, Clone)]
pub struct ResultPage {
    pub title: String,
    pub url: String,
    pub similarity_score: f32,
    pub domain_credibility_score: f32,
    pub rank: i8
}