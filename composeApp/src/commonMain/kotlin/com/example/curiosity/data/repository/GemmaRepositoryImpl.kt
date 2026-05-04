package com.example.curiosity.data.repository

import com.example.curiosity.data.remote.GemmaApiService
import com.example.curiosity.domain.repository.GemmaRepository

class GemmaRepositoryImpl(private val apiService: GemmaApiService): GemmaRepository {
    override suspend fun getSearchTitle(query: String): String {
        return apiService.generateSearchTitle(query)
    }
    override suspend fun getSearchSuggestion(query: String): String {
        return apiService.generateSearchSuggestion(query)
    }
    override suspend fun getMultiTurnConversationChat(chats: List<Pair<String, String>>): String {
        return apiService.generateMultiTurnConversationChat(chats)
    }
}