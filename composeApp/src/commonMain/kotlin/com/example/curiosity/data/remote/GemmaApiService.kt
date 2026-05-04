package com.example.curiosity.data.remote

interface GemmaApiService {
    suspend fun generateSearchTitle(query: String): String
    suspend fun generateSearchSuggestion(query: String): String
    suspend fun generateMultiTurnConversationChat(chats: List<Pair<String, String>>): String
}