package com.example.curiosity.domain.usecase

import com.example.curiosity.domain.models.IndexResponseData
import com.example.curiosity.domain.repository.IndexRepository

class IndexResultsUseCase(private val repository: IndexRepository) {
    suspend operator fun invoke(query: String): IndexResponseData {
        val country = getUserCountryCode()
        return repository.getIndexedResults(query, country)
    }
}