package com.example.curiosity.domain.repository

import com.example.curiosity.domain.models.CrawlResponseData
import kotlinx.coroutines.flow.Flow

interface CrawlRepository {
    fun getCrawledResults(query: String, country: String): Flow<CrawlResponseData>
}