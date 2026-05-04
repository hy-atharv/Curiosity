package com.example.curiosity.data.repository

import com.example.curiosity.data.mapper.toData
import com.example.curiosity.data.remote.IndexerApiService
import com.example.curiosity.domain.models.IndexResponseData
import com.example.curiosity.domain.repository.IndexRepository

class IndexRepositoryImpl(private val apiService: IndexerApiService): IndexRepository {
    override suspend fun getIndexedResults(query: String, country: String): IndexResponseData {
        return apiService.indexResults(query, country).toData()
    }
}