package com.example.curiosity.domain.models

import com.example.curiosity.core.models.Fact
import com.example.curiosity.core.models.FactRelationship
import com.example.curiosity.core.models.QueryTypes
import com.example.curiosity.core.models.StatusTypes
import kotlinx.serialization.Serializable

@Serializable
data class IndexResponseData(
    val count: Int,
    val indexed_results: List<IndexedResult>,
    val status_type: StatusTypes,
    val status_message: String
)

@Serializable
data class IndexedResult(
    val id: Int,
    val payload: Payload,
    val score: Double
)

@Serializable
data class Payload(
    val crawl_metadata: CrawlMetadata,
    val results_metadata: ResultsMetadata,
    val source_metadata: SourceMetadata
)

@Serializable
data class CrawlMetadata(
    val content_keywords: List<String>,
    val from_date: String,
    val query_type: List<QueryTypes>,
    val seed_urls: List<String>,
    val to_date: String,
    val topics: List<String>
)

@Serializable
data class ResultsMetadata(
    val fact_relationships: List<FactRelationship>,
    val facts: List<Fact>,
    val overall_answer_and_summary: String
)

@Serializable
data class SourceMetadata(
    val credibility_reason: String,
    val date: String,
    val summary: String,
    val title: String,
    val url: String
)
