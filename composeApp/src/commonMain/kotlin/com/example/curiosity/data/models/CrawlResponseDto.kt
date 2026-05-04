package com.example.curiosity.data.models

import com.example.curiosity.core.models.StatusTypes
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class CrawlResponseDto(
    val count: Int,
    val crawled_results: JsonElement,
    val status_type: StatusTypes,
    val status_message: String
)
