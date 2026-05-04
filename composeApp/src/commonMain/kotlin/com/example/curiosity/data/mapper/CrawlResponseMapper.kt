package com.example.curiosity.data.mapper


import com.example.curiosity.core.models.Fact
import com.example.curiosity.core.models.FactRelationship
import com.example.curiosity.core.models.FactRelationshipTypes
import com.example.curiosity.data.models.CrawlResponseDto
import com.example.curiosity.domain.models.CrawlResponseData
import com.example.curiosity.domain.models.CrawledResults
import com.example.curiosity.domain.models.DetailedSearchResult
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun CrawlResponseDto.toData(): CrawlResponseData {
    val crawledResultsObj = crawled_results.jsonObject
    return CrawlResponseData(
        count = count,
        status_type = status_type,
        status_message = status_message,
        crawled_results = CrawledResults(
            overall_answer_and_summary =
                crawledResultsObj["overall_answer_and_summary"]
                    ?.jsonPrimitive
                    ?.content
                    .orEmpty(),

            detailed_search_results =
                crawledResultsObj["detailed_search_results"]
                    ?.jsonArray
                    ?.map { it.toDetailedSearchResult() }
                    .orEmpty(),

            facts =
                crawledResultsObj["facts"]
                    ?.jsonArray
                    ?.map { it.toFact() }
                    .orEmpty(),

            fact_relationships =
                crawledResultsObj["fact_relationships"]
                    ?.jsonArray
                    ?.map { it.toFactRelationship() }
                    .orEmpty()
        )
    )
}

private fun JsonElement.toDetailedSearchResult(): DetailedSearchResult {
    val obj = jsonObject
    return DetailedSearchResult(
        url = obj["url"]?.jsonPrimitive?.content.orEmpty(),
        title = obj["title"]?.jsonPrimitive?.content.orEmpty(),
        summary = obj["summary"]?.jsonPrimitive?.content.orEmpty(),
        date = obj["date"]?.jsonPrimitive?.content.orEmpty(),
        credibility_reason = obj["credibility_reason"]?.jsonPrimitive?.content.orEmpty()
    )
}

fun JsonElement.toFact(): Fact {
    val obj = jsonObject
    return Fact(
        claim = obj["claim"]?.jsonPrimitive?.content.orEmpty(),
        evidence_urls =
            obj["evidence_urls"]
                ?.jsonArray
                ?.map { it.jsonPrimitive.content }
                .orEmpty()
    )
}

fun JsonElement.toFactRelationship(): FactRelationship {
    val obj = jsonObject
    return FactRelationship(
        source_claim = obj["source_claim"]?.jsonPrimitive?.content.orEmpty(),
        target_claim = obj["target_claim"]?.jsonPrimitive?.content.orEmpty(),
        connection_type =
            obj["connection_type"]
                ?.jsonPrimitive
                ?.content
                ?.let { FactRelationshipTypes.valueOf(it) }
                ?: FactRelationshipTypes.UNKNOWN
    )
}
