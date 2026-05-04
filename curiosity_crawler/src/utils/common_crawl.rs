use chrono::{NaiveDate, NaiveDateTime};
use futures_util::{StreamExt, TryStreamExt};
use reqwest::{Client, Error};
use tokio_util::codec::{FramedRead, LinesCodec};
use tokio_util::io::StreamReader;
use crate::models::cc_index_info::{CCIndexInfo, CCIndexRecord};

pub async fn enumerate_cc_indexes(
    seed_urls: &[String],
    from_date: &str,
    to_date: &str,
    client: &Client
) -> Result<Vec<String>, Error> {

    let cc_indexes_res = client
        .get("https://index.commoncrawl.org/collinfo.json")
        .send()
        .await?;

    let cc_indexes = cc_indexes_res.json::<Vec<CCIndexInfo>>().await?;

    let latest_cc_index = cc_indexes
        .first()
        .cloned()
        .unwrap_or_else(|| default_latest());

    let parsed_from_date = NaiveDate::parse_from_str(from_date, "%Y-%m-%d")
        .ok()
        .and_then(|d| d.and_hms_opt(0, 0, 0))
        .unwrap_or_else(|| default_oldest().from);

    let parsed_to_date = NaiveDate::parse_from_str(to_date, "%Y-%m-%d")
        .ok()
        .and_then(|d| d.and_hms_opt(23, 59, 59))
        .unwrap_or_else(|| default_latest().to);

    let time_window_cc_index = cc_indexes
        .iter()
        .find(|index| {
            index.to >= parsed_to_date &&
                index.from <= parsed_from_date
        })
        .cloned()
        .unwrap_or_else(|| {
            if parsed_to_date > cc_indexes.first().unwrap().to {
                default_latest()
            } else if parsed_from_date < cc_indexes.last().unwrap().from {
                default_oldest()
            } else {
                default_latest()
            }
        });

    let mut valid_seed_sources: Vec<String> = vec![];

    if latest_cc_index.id == time_window_cc_index.id {  // Only Enumerate Latest Index
        let index_api = latest_cc_index.cdx_api;

        for url in seed_urls {
            let index_res = client
                .get(&index_api)
                .query(&[
                    ("url", url.as_str()),
                    ("output", "json"),
                    ("matchType", "prefix"),
                    ("filter", "=mime:text/html"),
                    ("filter", "=status:200"),
                ])
                .send()
                .await?;

            let stream = index_res.bytes_stream();
            let reader = StreamReader::new(stream.map_err(|e| std::io::Error::new(std::io::ErrorKind::Other, e)));
            let mut lines = FramedRead::new(reader, LinesCodec::new());

            while let Some(line_res) = lines.next().await {
                let line = match line_res {
                    Ok(l) => l,
                    Err(e) => {
                        eprintln!("CDX API Response Stream Line decode error: {}", e);
                        continue;
                    }
                };
                let record: CCIndexRecord = match serde_json::from_str(&line) {
                    Ok(r) => r,
                    Err(e) => {
                        eprintln!("CC RECORD JSON parse error: {}", e);
                        continue;
                    }
                };
                // Keeping Valid Seed Source
                valid_seed_sources.push(record.url);
            }
        }
    }
    else {  // Enumerate both latest and time window indexes
        let latest_index_api = latest_cc_index.cdx_api; // Latest Index

        for url in seed_urls {
            let index_res = client
                .get(&latest_index_api)
                .query(&[
                    ("url", url.as_str()),
                    ("output", "json"),
                    ("matchType", "prefix"),
                    ("filter", "=mime:text/html"),
                    ("filter", "=status:200"),
                ])
                .send()
                .await?;

            let stream = index_res.bytes_stream();
            let reader = StreamReader::new(stream.map_err(|e| std::io::Error::new(std::io::ErrorKind::Other, e)));
            let mut lines = FramedRead::new(reader, LinesCodec::new());

            while let Some(line_res) = lines.next().await {
                let line = match line_res {
                    Ok(l) => l,
                    Err(e) => {
                        eprintln!("CDX API Response Stream Line decode error: {}", e);
                        continue;
                    }
                };
                let record: CCIndexRecord = match serde_json::from_str(&line) {
                    Ok(r) => r,
                    Err(e) => {
                        eprintln!("CC RECORD JSON parse error: {}", e);
                        continue;
                    }
                };
                // Keeping Valid Seed Source
                valid_seed_sources.push(record.url);
            }
        }
        
        let time_window_index_api = time_window_cc_index.cdx_api; // Time Window Index

        for url in seed_urls {
            let index_res = client
                .get(&time_window_index_api)
                .query(&[
                    ("url", url.as_str()),
                    ("output", "json"),
                    ("matchType", "prefix"),
                    ("filter", "=mime:text/html"),
                    ("filter", "=status:200"),
                ])
                .send()
                .await?;

            let stream = index_res.bytes_stream();
            let reader = StreamReader::new(stream.map_err(|e| std::io::Error::new(std::io::ErrorKind::Other, e)));
            let mut lines = FramedRead::new(reader, LinesCodec::new());

            while let Some(line_res) = lines.next().await {
                let line = match line_res {
                    Ok(l) => l,
                    Err(e) => {
                        eprintln!("CDX API Response Stream Line decode error: {}", e);
                        continue;
                    }
                };
                let record: CCIndexRecord = match serde_json::from_str(&line) {
                    Ok(r) => r,
                    Err(e) => {
                        eprintln!("CC RECORD JSON parse error: {}", e);
                        continue;
                    }
                };
                // Keeping Valid Seed Source
                valid_seed_sources.push(record.url);
            }
        }

    }

    Ok(valid_seed_sources)
}

fn default_latest() -> CCIndexInfo {
    CCIndexInfo {
        id: "CC-MAIN-2026-04".to_string(),
        name: "January 2026 Index".to_string(),
        timegate: "https://index.commoncrawl.org/CC-MAIN-2026-04/".to_string(),
        cdx_api: "https://index.commoncrawl.org/CC-MAIN-2026-04-index".to_string(),
        from: NaiveDateTime::parse_from_str(
            "2026-01-12T16:12:39",
            "%Y-%m-%dT%H:%M:%S",
        ).unwrap(),
        to: NaiveDateTime::parse_from_str(
            "2026-01-25T14:05:40",
            "%Y-%m-%dT%H:%M:%S",
        ).unwrap(),
    }
}

fn default_oldest() -> CCIndexInfo {
    CCIndexInfo {
        id: "CC-MAIN-2008-2009".to_string(),
        name: "2008 - 2009 ARC Files Index".to_string(),
        timegate: "https://index.commoncrawl.org/CC-MAIN-2008-2009/".to_string(),
        cdx_api: "https://index.commoncrawl.org/CC-MAIN-2008-2009-index".to_string(),
        from: NaiveDateTime::parse_from_str(
            "2008-05-09T05:37:12",
            "%Y-%m-%dT%H:%M:%S",
        ).unwrap(),
        to: NaiveDateTime::parse_from_str(
            "2009-01-09T20:58:29",
            "%Y-%m-%dT%H:%M:%S",
        ).unwrap(),
    }
}