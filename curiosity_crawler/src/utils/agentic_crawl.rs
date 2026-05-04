use std::env;
use dotenv::dotenv;
use reqwest::{Client, Error};
use serde_json::{json, Value};
use crate::dto::model_dto::StructuredAgenticCrawlData;
use crate::dto::request_dto::CrawlRequestData;
use crate::dto::response_dto::CrawlResultsData;

pub async fn agentic_crawl(crawl_request_data: CrawlRequestData) -> Result<CrawlResultsData, Error> {
    dotenv().ok();
    let api_key = env::var("GEMINI_API_KEY").unwrap_or_else(|_| "".to_string());

    let endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    let client = Client::new();

    let body = json!({
      "system_instruction": {
        "parts": [
          {
            "text": "You are an advanced agentic web research and crawling intelligence engine.

              Your responsibilities:

              - Actively use Google Search and URL Context tools.
              - Retrieve credible and relevant web sources.
              - Synthesize a concise and evidence-backed answer to the user's query.
              - Extract structured factual claims supported by explicit evidence.
              - Construct logical relationships between factual claims as graph edges.

              STRICT RULES:

              1. You MUST use search tools before answering.
              2. Do NOT hallucinate URLs. Every URL must be real and retrievable.
              3. All factual claims must be supported by evidence_urls.
              4. All evidence_urls must appear in detailed_search_results.
              5. All dates MUST be in strict ISO 8601 format (YYYY-MM-DD).
              6. credibility_reason must justify trustworthiness using domain authority,
                 institutional reputation, editorial standards, author expertise,
                 citation quality, or publication credibility.
              7. fact_relationships must logically connect claims present in the facts array.
              8. connection_type MUST be one of:
                 SUPPORTS, CONTRADICTS, ELABORATES, CAUSES
              9. Do NOT include explanations, markdown, or extra fields.
              10. Output MUST strictly follow the provided JSON schema.
              11. overall_answer_and_summary must be concise (4-5 lines), directly answer the query,
                  and be grounded in retrieved evidence."
          }
        ]
      },
      "contents": [
        {
          "parts": [
            {
              "text": format!(
                "User Search Query: {}\nEnriched Search Query: {}\nSeed URLs: {:?}",
                crawl_request_data.query,
                crawl_request_data.enriched_query,
                crawl_request_data.expanded_query_data.seed_urls
              )
            }
          ]
        }
      ],
      "generationConfig": {
        "responseMimeType": "application/json",
        "responseJsonSchema": {
          "type": "object",
          "description": "Structured agentic crawl output containing summarized answer, verified search results, extracted factual claims, and graph-based relationships between claims.",
          "properties": {
            "overall_answer_and_summary": {
              "type": "string",
              "description": "A concise 4-5 line direct answer synthesizing all verified search results."
            },
            "detailed_search_results": {
              "type": "array",
              "description": "Structured list of verified and relevant search results retrieved using tools.",
              "items": {
                "type": "object",
                "description": "A single verified search result.",
                "properties": {
                  "url": {
                    "type": "string",
                    "description": "The exact URL of the retrieved source."
                  },
                  "title": {
                    "type": "string",
                    "description": "Official title of the webpage or article."
                  },
                  "summary": {
                    "type": "string",
                    "description": "Focused 4-5 line summary relevant to the user's query."
                  },
                  "date": {
                    "type": "string",
                    "description": "Publication date in strict ISO 8601 format (YYYY-MM-DD)."
                  },
                  "credibility_reason": {
                    "type": "string",
                    "description": "Structured reasoning explaining why this source is trustworthy."
                  }
                },
                "required": [
                  "url",
                  "title",
                  "summary",
                  "date",
                  "credibility_reason"
                ]
              }
            },
            "facts": {
              "type": "array",
              "description": "List of extracted factual claims supported by evidence.",
              "items": {
                "type": "object",
                "description": "A factual claim derived from verified sources.",
                "properties": {
                  "claim": {
                    "type": "string",
                    "description": "A clearly stated factual assertion relevant to the query."
                  },
                  "evidence_urls": {
                    "type": "array",
                    "description": "URLs from detailed_search_results that support this claim.",
                    "items": {
                      "type": "string",
                      "description": "A URL providing direct evidence."
                    }
                  }
                },
                "required": [
                  "claim",
                  "evidence_urls"
                ]
              }
            },
            "fact_relationships": {
              "type": "array",
              "description": "Graph edges defining logical relationships between factual claims.",
              "items": {
                "type": "object",
                "description": "A relationship between two factual claims.",
                "properties": {
                  "source_claim": {
                    "type": "string",
                    "description": "The originating factual claim."
                  },
                  "target_claim": {
                    "type": "string",
                    "description": "The related factual claim connected to the source."
                  },
                  "connection_type": {
                    "type": "string",
                    "description": "Logical relationship between source and target claim.",
                    "enum": [
                      "SUPPORTS",
                      "CONTRADICTS",
                      "ELABORATES",
                      "CAUSES"
                    ]
                  }
                },
                "required": [
                  "source_claim",
                  "target_claim",
                  "connection_type"
                ]
              }
            }
          },
          "required": [
            "overall_answer_and_summary",
            "detailed_search_results",
            "facts",
            "fact_relationships"
          ]
        }
      }
    });

    let res = client
        .post(endpoint)
        .header("x-goog-api-key", &api_key)
        .json(&body)
        .send()
        .await?
        .json::<Value>()
        .await?;

    println!("{:#?}", res);

    let structured_data_text = res
        .get("candidates")
        .and_then(|c| c.get(0))
        .and_then(|c| c.get("content"))
        .and_then(|c| c.get("parts"))
        .and_then(|p| p.get(0))
        .and_then(|p| p.get("text"))
        .and_then(|t| t.as_str())
        .unwrap_or("");

    let mut structured_data_option: Option<StructuredAgenticCrawlData> =
        serde_json::from_str(structured_data_text).ok();
  
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
  
    if let Some(structured_data) = structured_data_option {
      println!("{:#?}", structured_data);
      Ok(
        CrawlResultsData{
          count: structured_data.detailed_search_results.len(),
          data: Some(structured_data),
          crawl_metadata: crawl_request_data.expanded_query_data
        }
      )
    } else {
      println!("Structured Output Failed after retries.");
      Ok(
        CrawlResultsData{
          count: 0,
          data: None,
          crawl_metadata: crawl_request_data.expanded_query_data
        }
      )
    }
}