use serde::{Serialize, Deserialize};

#[derive(Serialize, Deserialize)]
pub struct TermWeights {
    pub original_query_weight: f32,
    pub enriched_query_weight: f32,
    pub content_keywords_weight: f32,
    pub topics_weight: f32
}