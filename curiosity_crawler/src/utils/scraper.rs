use std::io;
use std::io::ErrorKind;
use reqwest::{Client, Error, StatusCode};
use scraper::{Html, Selector};
use crate::dto::request_dto::StructuredQueryExpandedData;
use crate::dto::response_dto::CrawlResultsData;
use crate::models::pages::PageContent;

pub async fn scrape_page(
    client: &Client,
    url: &str,
) -> Result<PageContent, Error> {

    let page_res = client
        .get(url)
        .send()
        .await?
        .error_for_status()?;

    let page_content_text = page_res.text().await?;

    // Parse the HTML
    let document = Html::parse_document(&page_content_text);

    // Extract Title
    let title_selector = Selector::parse("title").unwrap();
    let title = document
        .select(&title_selector)
        .next()
        .map(|e| e.text().collect::<String>())
        .unwrap_or_default();

    // Extract Metadata
    let meta_selector = Selector::parse("meta[name='description']").unwrap();
    let metadata = document
        .select(&meta_selector)
        .next()
        .and_then(|e| e.value().attr("content"))
        .unwrap_or("")
        .to_string();

    // Extract Headers
    let header_selector = Selector::parse("h1, h2, h3").unwrap();
    let headers = document
        .select(&header_selector)
        .map(|e| e.text().collect::<String>())
        .collect::<Vec<_>>()
        .join(" | ");

    // Extract Main Content
    let p_selector = Selector::parse("p").unwrap();
    let content = document
        .select(&p_selector)
        .map(|e| e.text().collect::<String>())
        .collect::<Vec<_>>()
        .join("\n");

    // Extract Links
    let link_selector = Selector::parse("a[href]").unwrap();
    let links = document
        .select(&link_selector)
        .filter_map(|e| e.value().attr("href"))
        .map(|s| s.to_string())
        .collect::<Vec<String>>();

    Ok(PageContent {
        title,
        metadata,
        headers,
        content,
        links,
    })
}


pub async fn initiate_recursive_crawl_algorithm(
    seed_sources: Vec<String>,
    query: &str,
    enriched_query: &str,
    expanded_query_data: &StructuredQueryExpandedData
) -> Option<CrawlResultsData> {
    
    Some(
        CrawlResultsData{
            count: 0,
            data: None,
            crawl_metadata: expanded_query_data.to_owned(),
        }
    )
}