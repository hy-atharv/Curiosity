package com.example.curiosity.domain.models

import com.example.curiosity.core.models.Fact
import com.example.curiosity.core.models.FactRelationship
import com.example.curiosity.core.models.StatusTypes
import kotlinx.serialization.Serializable

@Serializable
data class CrawlResponseData(
    val count: Int,
    val crawled_results: CrawledResults,
    val status_type: StatusTypes,
    val status_message: String
)

@Serializable
data class CrawledResults(
    val overall_answer_and_summary: String,
    val detailed_search_results: List<DetailedSearchResult>,
    val facts: List<Fact>,
    val fact_relationships: List<FactRelationship>
)

@Serializable
data class DetailedSearchResult(
    val url: String,
    val title: String,
    val summary: String,
    val date: String,
    val credibility_reason: String
)
