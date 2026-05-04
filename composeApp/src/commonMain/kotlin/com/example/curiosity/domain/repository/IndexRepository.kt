package com.example.curiosity.domain.repository

import com.example.curiosity.domain.models.IndexResponseData

interface IndexRepository {
    suspend fun getIndexedResults(query: String, country: String): IndexResponseData
}