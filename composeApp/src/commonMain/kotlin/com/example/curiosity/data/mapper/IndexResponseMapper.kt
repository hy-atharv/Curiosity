package com.example.curiosity.data.mapper

import com.example.curiosity.core.models.QueryTypes
import com.example.curiosity.data.models.IndexResponseDto
import com.example.curiosity.domain.models.CrawlMetadata
import com.example.curiosity.domain.models.IndexResponseData
import com.example.curiosity.domain.models.IndexedResult
import com.example.curiosity.domain.models.Payload
import com.example.curiosity.domain.models.ResultsMetadata
import com.example.curiosity.domain.models.SourceMetadata
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun IndexResponseDto.toData(): IndexResponseData {
    return IndexResponseData(
        count = count,
        status_type = status_type,
        status_message = status_message,
        indexed_results =
            indexed_results.map { element ->
                val root = element.jsonObject

                IndexedResult(
                    id =
                        root["id"]
                            ?.jsonPrimitive
                            ?.int
                            ?: -1,

                    payload =
                        root["payload"]
                            ?.jsonObject
                            ?.let { payload ->
                                Payload(
                                    crawl_metadata =
                                        payload["crawl_metadata"]
                                            ?.toCrawlMetadata()
                                            ?: CrawlMetadata(
                                                emptyList(),
                                                "",
                                                emptyList(),
                                                emptyList(),
                                                "",
                                                emptyList()
                                            ),

                                    results_metadata =
                                        payload["results_metadata"]
                                            ?.toResultsMetadata()
                                            ?: ResultsMetadata(
                                                emptyList(),
                                                emptyList(),
                                                ""
                                            ),

                                    source_metadata =
                                        payload["source_metadata"]
                                            ?.toSourceMetadata()
                                            ?: SourceMetadata(
                                                "",
                                                "",
                                                "",
                                                "",
                                                ""
                                            )
                                )
                            }
                            ?: Payload(
                                CrawlMetadata(
                                    emptyList(),
                                    "",
                                    emptyList(),
                                    emptyList(),
                                    "",
                                    emptyList()
                                ),
                                ResultsMetadata(
                                    emptyList(),
                                    emptyList(),
                                    ""
                                ),
                                SourceMetadata(
                                    "",
                                    "",
                                    "",
                                    "",
                                    ""
                                )
                            ),

                    score =
                        root["score"]
                            ?.jsonPrimitive
                            ?.double
                            ?: 0.0
                )
            }
    )
}

private fun JsonElement.toCrawlMetadata(): CrawlMetadata {
    val obj = jsonObject
    return CrawlMetadata(
        content_keywords =
            obj["content_keywords"]
                ?.jsonArray
                ?.map { it.jsonPrimitive.content }
                .orEmpty(),

        from_date =
            obj["from_date"]
                ?.jsonPrimitive
                ?.content
                .orEmpty(),

        query_type =
            obj["query_type"]
                ?.jsonArray
                ?.map {
                    QueryTypes.valueOf(it.jsonPrimitive.content)
                }
                .orEmpty(),

        seed_urls =
            obj["seed_urls"]
                ?.jsonArray
                ?.map { it.jsonPrimitive.content }
                .orEmpty(),

        to_date =
            obj["to_date"]
                ?.jsonPrimitive
                ?.content
                .orEmpty(),

        topics =
            obj["topics"]
                ?.jsonArray
                ?.map { it.jsonPrimitive.content }
                .orEmpty()
    )
}

private fun JsonElement.toResultsMetadata(): ResultsMetadata {
    val obj = jsonObject
    return ResultsMetadata(
        fact_relationships =
            obj["fact_relationships"]
                ?.jsonArray
                ?.map { it.toFactRelationship() }
                .orEmpty(),

        facts =
            obj["facts"]
                ?.jsonArray
                ?.map { it.toFact() }
                .orEmpty(),

        overall_answer_and_summary =
            obj["overall_answer_and_summary"]
                ?.jsonPrimitive
                ?.content
                .orEmpty()
    )
}

private fun JsonElement.toSourceMetadata(): SourceMetadata {
    val obj = jsonObject
    return SourceMetadata(
        credibility_reason =
            obj["credibility_reason"]
                ?.jsonPrimitive
                ?.content
                .orEmpty(),

        date =
            obj["date"]
                ?.jsonPrimitive
                ?.content
                .orEmpty(),

        summary =
            obj["summary"]
                ?.jsonPrimitive
                ?.content
                .orEmpty(),

        title =
            obj["title"]
                ?.jsonPrimitive
                ?.content
                .orEmpty(),

        url =
            obj["url"]
                ?.jsonPrimitive
                ?.content
                .orEmpty()
    )
}