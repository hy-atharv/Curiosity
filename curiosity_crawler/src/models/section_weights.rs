use serde::{Serialize, Deserialize};

#[derive(Serialize, Deserialize)]
pub struct SectionWeights {
    pub title_weight: f32,
    pub metadata_weight: f32,
    pub headers_weight: f32,
    pub content_weight: f32
}