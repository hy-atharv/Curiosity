use std::collections::HashSet;
use bm25::{Embedder, EmbedderBuilder, Language, Scorer};

pub fn calculate_section_score(
    original_query: &str,
    enriched_query: &str,
    content_keywords: Vec<String>,
    topics: Vec<String>,
    original_query_weight: f32,
    enriched_query_weight: f32,
    content_keywords_weight: f32,
    topics_weight: f32,
    section_content: &str
) -> f32 {
    let mut scorer = Scorer::<usize>::new();
    let section_embedder: Embedder =
        EmbedderBuilder::with_fit_to_corpus(Language::English, &[section_content]).build();
    scorer.upsert(&0, section_embedder.embed(section_content));

    let original_query_keywords = split_into_keywords(original_query, &content_keywords);
    let enriched_query_keywords = split_into_keywords(enriched_query, &content_keywords);

    let mut seen_keywords = HashSet::new();

    let mut final_score:f32 = 0.0;

    // ORIGINAL QUERY MATCH SCORE
    for term in original_query_keywords.iter() {
        if seen_keywords.insert(term) {
            let score_res = scorer.score(&0, &section_embedder.embed(term));
            if let Some(score) = score_res {
                final_score += score * original_query_weight;
            }
        }
    }

    // ENRICHED QUERY MATCH SCORE
    for term in enriched_query_keywords.iter() {
        if seen_keywords.insert(term) {
            let score_res = scorer.score(&0, &section_embedder.embed(term));
            if let Some(score) = score_res {
                final_score += score * enriched_query_weight;
            }
        }
    }

    // CONTENT KEYWORDS MATCH SCORE
    for term in content_keywords.iter() {
        if seen_keywords.insert(term) {
            let score_res = scorer.score(&0, &section_embedder.embed(term));
            if let Some(score) = score_res {
                final_score += score * content_keywords_weight;
            }
        }
    }

    // TOPICS MATCH SCORE
    for term in topics.iter() {
        if seen_keywords.insert(term) {
            let score_res = scorer.score(&0, &section_embedder.embed(term));
            if let Some(score) = score_res {
                final_score += score * topics_weight;
            }
        }
    }

    final_score
}


pub fn calculate_page_score(
    bm25_title_score: f32,
    bm25_metadata_score: f32,
    bm25_headers_score: f32,
    bm25_content_score: f32,
    title_weight: f32,
    metadata_weight: f32,
    headers_weight: f32,
    content_weight: f32,
) -> f32 {
    let final_page_score: f32 = (bm25_title_score * title_weight)
    + (bm25_metadata_score * metadata_weight)
    + (bm25_headers_score * headers_weight)
    + (bm25_content_score * content_weight);

    final_page_score
}

fn split_into_keywords(query: &str, content_keywords: &Vec<String>) -> Vec<String> {
    // ---------- Normalize ----------
    let normalized = query
        .to_lowercase()
        .chars()
        .map(|c| if c.is_alphanumeric() || c.is_whitespace() { c } else { ' ' })
        .collect::<String>()
        .split_whitespace()
        .collect::<Vec<_>>()
        .join(" ");

    // ---------- Stopword removal ----------
    let stopwords = stop_words::get(stop_words::LANGUAGE::English);

    let tokens: Vec<String> = normalized
        .split_whitespace()
        .filter(|word| !stopwords.contains(word))
        .map(|word| word.to_string())
        .collect();

    // ---------- Generate n-grams ----------
    let unigrams = tokens.clone();
    let bigrams = ngrams(&tokens, 2);
    let trigrams = ngrams(&tokens, 3);

    let keyword_anchors = build_keyword_anchor_set(content_keywords);

    // ---------- Filter grams ----------
    let filtered_unigrams = filter_unigrams_with_keywords(unigrams, &keyword_anchors);
    let filtered_bigrams = filter_ngrams_with_keywords(bigrams, &keyword_anchors);
    let filtered_trigrams = filter_ngrams_with_keywords(trigrams, &keyword_anchors);

    // ---------- Merge + deduplicate + cap ----------
    merge_and_cap(
        filtered_unigrams,
        filtered_bigrams,
        filtered_trigrams,
        30,
    )
}

fn ngrams(tokens: &[String], n: usize) -> Vec<String> {
    if tokens.len() < n {
        return Vec::new();
    }
    tokens
        .windows(n)
        .map(|w| w.join(" "))
        .collect()
}

fn build_keyword_anchor_set(keywords: &Vec<String>) -> HashSet<String> {
    keywords
        .iter()
        .map(|k| {
            k.to_lowercase()
                .chars()
                .map(|c| if c.is_alphanumeric() || c.is_whitespace() { c } else { ' ' })
                .collect::<String>()
                .split_whitespace()
                .collect::<Vec<_>>()
                .join(" ")
        })
        .collect()
}

fn filter_unigrams_with_keywords(
    tokens: Vec<String>,
    keyword_anchors: &HashSet<String>,
) -> Vec<String> {
    tokens
        .into_iter()
        .filter(|t| {
            keyword_anchors.contains(t)
                || keyword_anchors.iter().any(|k| k.contains(t))
        })
        .collect()
}

fn filter_ngrams_with_keywords(
    ngrams: Vec<String>,
    keyword_anchors: &HashSet<String>,
) -> Vec<String> {
    ngrams
        .into_iter()
        .filter(|g| {
            keyword_anchors.contains(g)
                || keyword_anchors.iter().any(|k| k.contains(g) || g.contains(k))
        })
        .collect()
}

fn merge_and_cap(
    unigrams: Vec<String>,
    bigrams: Vec<String>,
    trigrams: Vec<String>,
    max_terms: usize,
) -> Vec<String> {
    use std::collections::HashSet;

    let mut seen = HashSet::new();
    let mut result = Vec::new();

    for term in trigrams
        .into_iter()
        .chain(bigrams)
        .chain(unigrams)
    {
        if seen.insert(term.clone()) {
            result.push(term);
        }
        if result.len() >= max_terms {
            break;
        }
    }

    result
}