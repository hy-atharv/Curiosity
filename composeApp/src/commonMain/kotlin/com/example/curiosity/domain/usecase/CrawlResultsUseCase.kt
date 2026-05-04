package com.example.curiosity.domain.usecase

import com.example.curiosity.domain.models.CrawlResponseData
import com.example.curiosity.domain.repository.CrawlRepository
import kotlinx.coroutines.flow.Flow

class CrawlResultsUseCase(private val repository: CrawlRepository) {
    operator fun invoke(query: String): Flow<CrawlResponseData> {
        val country = getUserCountryCode()
        return repository.getCrawledResults(query, country)
    }
}