package com.example.curiosity.core.models

import com.example.curiosity.domain.models.CrawlResponseData
import com.example.curiosity.domain.models.IndexResponseData

sealed class UiStates {
    data object InitialState: UiStates()
    data class IndexResults(val data: IndexResponseData) : UiStates()
    data class CrawlResults(val data: CrawlResponseData) : UiStates()
    data class GemmaResult(val data: String) : UiStates()
    data class IndexerError(val message: String) : UiStates()
    data class CrawlerError(val message: String): UiStates()
    data class GemmaError(val message: String): UiStates()
    data class EmptyResults(val message: String): UiStates()
}