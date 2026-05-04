package com.example.curiosity.data.remote

import com.example.curiosity.data.models.IndexResponseDto
import com.example.curiosity.domain.models.IndexResponseData

interface IndexerApiService {
    suspend fun indexResults(query: String, country: String): IndexResponseDto
}