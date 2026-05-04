use dotenv::dotenv;
use std::env;
use reqwest::{Client, Error};
use serde_json::{json, Value};
use crate::dto::model_dto::StructuredQueryExpandedData;

pub async fn enrich_query(query: &str, country: &str) -> Result<String, Error> {
    // Enrich User Query to make it more effective for searching
    dotenv().ok();
    let api_key = env::var("GEMMA_API_KEY").unwrap_or_else(|_| "".to_string());

    let endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent";

    let client = Client::new();

    let body = json!({
        "contents": [
            {
                "role": "user",
                "parts": [
                    {
                        "text": format!("Input Query: {}\nCountry: {}", query, country)
                    }
                ]
            }
        ],
        "system_instruction": {
            "parts": [
                {
                    "text": "You are a search query enrichment engine for a web crawler.
                            Your task is to EXPAND the input query to improve search recall and precision. 
                            Instructions: 
                            - Preserve the original intent exactly. 
                            - Add relevant keywords, synonyms, related technical terms, and contextual phrases. 
                            - Expand abbreviations and implicit concepts. 
                            - Include commonly used alternative terms and morphisms. 
                            - Incorporate geographic or regional context when relevant. 
                            - Do NOT add explanations or commentary. 
                            - Output ONLY the enriched query as a single line of text."
                }
            ]
        },
        "generation_config": {
            "thinking_config": {
                "include_thoughts": false,
                "thinking_level": "minimal"
            }
        }
    });


    let res = client
        .post(endpoint)
        .header("x-goog-api-key", api_key)
        .json(&body)
        .send()
        .await?
        .json::<Value>()
        .await?;

    let enriched_query = res
        .get("candidates")
        .and_then(|c| c.get(0))
        .and_then(|c| c.get("content"))
        .and_then(|c| c.get("parts"))
        .and_then(|p| p.get(1))
        .and_then(|p| p.get("text"))
        .and_then(|t| t.as_str())
        .unwrap_or("")
        .trim()
        .to_string();

    Ok(enriched_query)
}


pub async fn expand_query(query: &str, enriched_query: &str) -> Result<Option<StructuredQueryExpandedData>, Error> {
    // Enrich User Query to make it more effective for searching
    dotenv().ok();
    let api_key = env::var("GEMINI_API_KEY").unwrap_or_else(|_| "".to_string());

    let endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    let client = Client::new();

    let body = json!({
        "system_instruction": {
          "parts": [
            {
              "text": "You are a web search analysis and crawling intelligence engine.

                    Your task is to analyze a user search query and its enriched variant to support a web search engine and crawler.

                    You must:
                    - Identify the main topical areas relevant for crawling.
                    - Classify the query intent into one or more query types.
                    - Suggest credible and relevant Seed URLs(web pages endpoints) to start crawl with.
                    - Generate comprehensive content keywords including synonyms, related phrases, and factual terminology.

                    Rules:
                    - Output MUST strictly follow the provided JSON schema.
                    - Use only enum values defined for query_type.
                    - Do NOT add explanations or extra fields.
                    - Ensure all arrays are non-empty when reasonable.
                    - Prefer authoritative and regionally relevant domains when applicable.
                    - Infer from_date and to_date in ISO 8601 (YYYY-MM-DD) format by using explicit dates if present in the query, resolving relative time references when mentioned, or otherwise inferring a reasonable, intent-appropriate date range ending at the current date; never output nulls, natural-language dates, or omit these fields.
                    "
            }
          ]
        },
        "contents": [
            {
                "parts": [
                    {
                          "text": format!("User search query: {}\nEnriched search query: {}",query, enriched_query)
                    }
                ]
            }
        ],
        "generationConfig": {
            "responseMimeType": "application/json",
            "responseJsonSchema": {
                  "type": "object",
                  "properties": {
                    "topics": {
                      "type": "array",
                      "items": { "type": "string" },
                      "description": "High-level topics the search query aligns with for optimized crawling."
                    },
                    "query_type": {
                      "type": "array",
                      "items": {
                        "type": "string",
                        "enum": ["EVENT", "REGIONAL", "INFORMATIONAL", "ENTITY"]
                      },
                      "description": "One or more categories classifying the intent of the search query."
                    },
                    "seed_urls": {
                      "type": "array",
                      "items": { "type": "string" },
                      "description": "Relevant and credible seed urls to crawl for this query."
                    },
                    "content_keywords": {
                      "type": "array",
                      "items": { "type": "string" },
                      "description": "Relevant keywords, synonyms, morphologically varied phrases, and factual terms expected in relevant web content."
                    },
                    "from_date": {
                      "type": "string",
                      "description": "Start date for relevant content in ISO 8601 format (YYYY-MM-DD). If no explicit date is mentioned in the query, infer a reasonable start date based on query intent."
                    },
                    "to_date": {
                      "type": "string",
                      "description": "End date for relevant content in ISO 8601 format (YYYY-MM-DD). If no explicit date is mentioned, infer a reasonable end date (often today's date for recent or ongoing topics)."
                    }
                  },
                  "required": [
                    "topics",
                    "query_type",
                    "seed_urls",
                    "content_keywords",
                    "from_date",
                    "to_date"
                  ]
            }
        },
    });

    let res = client
        .post(endpoint)
        .header("x-goog-api-key", &api_key)
        .json(&body)
        .send()
        .await?
        .json::<Value>()
        .await?;

    println!("{:?}", res);

    let structured_data_text = res
        .get("candidates")
        .and_then(|c| c.get(0))
        .and_then(|c| c.get("content"))
        .and_then(|c| c.get("parts"))
        .and_then(|p| p.get(0))
        .and_then(|p| p.get("text"))
        .and_then(|t| t.as_str())
        .unwrap_or("");

    let mut structured_data_option: Option<StructuredQueryExpandedData> =
        serde_json::from_str(structured_data_text).ok();
    
    println!("{:?}", structured_data_text);

    let mut attempts = 1;

    while structured_data_option.is_none() && attempts < 3 {
        println!("Structured parsing failed. Retrying... Attempt {}", attempts + 1);

        let retry_res = client
            .post(endpoint)
            .header("x-goog-api-key", &api_key)
            .json(&body)
            .send()
            .await?
            .json::<Value>()
            .await?;

        let retry_text = retry_res
            .get("candidates")
            .and_then(|c| c.get(0))
            .and_then(|c| c.get("content"))
            .and_then(|c| c.get("parts"))
            .and_then(|p| p.get(0))
            .and_then(|p| p.get("text"))
            .and_then(|t| t.as_str())
            .unwrap_or("");

        structured_data_option = serde_json::from_str(retry_text).ok();
        attempts += 1;
    }

    if let Some(structured_data) = &structured_data_option {
        println!("{:#?}", structured_data);
    }

    Ok(structured_data_option)
}
