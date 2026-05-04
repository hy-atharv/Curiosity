package com.example.curiosity.domain.repository

interface GemmaRepository {
    suspend fun getSearchTitle(query: String): String
    suspend fun getSearchSuggestion(query: String): String
    suspend fun getMultiTurnConversationChat(chats: List<Pair<String, String>>): String
}