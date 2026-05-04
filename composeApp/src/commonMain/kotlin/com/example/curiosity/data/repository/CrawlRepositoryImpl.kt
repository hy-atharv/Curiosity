package com.example.curiosity.data.repository

import com.example.curiosity.data.mapper.toData
import com.example.curiosity.data.remote.CrawlerApiService
import com.example.curiosity.domain.models.CrawlResponseData
import com.example.curiosity.domain.repository.CrawlRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CrawlRepositoryImpl(private val apiService: CrawlerApiService): CrawlRepository {
    override fun getCrawledResults(query: String, country: String): Flow<CrawlResponseData> = flow {
        apiService.streamCrawledResults(query, country)
            .collect{
                emit(it.toData())
            }
    }
}