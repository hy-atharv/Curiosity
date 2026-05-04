package com.example.curiosity.data.remote

import com.example.curiosity.data.models.CrawlResponseDto
import kotlinx.coroutines.flow.Flow

interface CrawlerApiService {
    fun streamCrawledResults(query: String, country: String): Flow<CrawlResponseDto>
}