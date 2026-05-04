use serde::{Deserialize, Serialize};
use crate::models::section_weights::SectionWeights;
use crate::models::term_weights::TermWeights;

#[derive(Serialize, Deserialize, Clone)]
pub enum QueryType {
    EVENT,
    REGIONAL,
    INFORMATIONAL,
    ENTITY
}

impl QueryType {
    fn term_weights(&self) -> TermWeights {
        match self {
            QueryType::EVENT => TermWeights {
                original_query_weight: 0.40,
                enriched_query_weight: 0.30,
                content_keywords_weight: 0.20,
                topics_weight: 0.10,
            },
            QueryType::REGIONAL => TermWeights {
                original_query_weight: 0.30,
                enriched_query_weight: 0.35,
                content_keywords_weight: 0.25,
                topics_weight: 0.10,
            },
            QueryType::INFORMATIONAL => TermWeights {
                original_query_weight: 0.20,
                enriched_query_weight: 0.35,
                content_keywords_weight: 0.30,
                topics_weight: 0.15,
            },
            QueryType::ENTITY => TermWeights {
                original_query_weight: 0.45,
                enriched_query_weight: 0.20,
                content_keywords_weight: 0.15,
                topics_weight: 0.20,
            },
        }
    }

    fn section_weights(&self) -> SectionWeights {
        match self {
            QueryType::EVENT => SectionWeights {
                title_weight: 0.40,
                metadata_weight: 0.15,
                headers_weight: 0.20,
                content_weight: 0.25,
            },
            QueryType::REGIONAL => SectionWeights {
                title_weight: 0.30,
                metadata_weight: 0.15,
                headers_weight: 0.20,
                content_weight: 0.35,
            },
            QueryType::INFORMATIONAL => SectionWeights {
                title_weight: 0.20,
                metadata_weight: 0.10,
                headers_weight: 0.25,
                content_weight: 0.45,
            },
            QueryType::ENTITY => SectionWeights {
                title_weight: 0.45,
                metadata_weight: 0.15,
                headers_weight: 0.20,
                content_weight: 0.20,
            },
        }
    }

    fn threshold_score(&self) -> f32 {
        match self {
            QueryType::EVENT => 0.60,
            QueryType::ENTITY => 0.55,
            QueryType::REGIONAL => 0.45,
            QueryType::INFORMATIONAL => 0.35,
        }
    }
}