package com.example.curiosity.domain.usecase

import com.example.curiosity.domain.repository.GemmaRepository

class GemmaUseCase(private val repository: GemmaRepository) {
    suspend fun getSearchTitle(query: String): String {
        return repository.getSearchTitle(query)
            // Remove bold/italic asterisks but keep the text inside: **text** -> text
            .replace(Regex("""\*+(.*?)\*+"""), "$1")
            // Remove bold/italic underscores: __text__ -> text
            .replace(Regex("""_+(.*?)_+"""), "$1")
            // Remove markdown headers: # Header -> Header
            .replace(Regex("""^#+\s+""", RegexOption.MULTILINE), "")
            // Remove inline code backticks: `code` -> code
            .replace(Regex("""`([^`]+)`"""), "$1")
            // Trim extra whitespace
            .trim()
    }
    suspend fun getSearchSuggestion(query: String): String {
        return repository.getSearchSuggestion(query)
            // Remove bold/italic asterisks but keep the text inside: **text** -> text
            .replace(Regex("""\*+(.*?)\*+"""), "$1")
            // Remove bold/italic underscores: __text__ -> text
            .replace(Regex("""_+(.*?)_+"""), "$1")
            // Remove markdown headers: # Header -> Header
            .replace(Regex("""^#+\s+""", RegexOption.MULTILINE), "")
            // Remove inline code backticks: `code` -> code
            .replace(Regex("""`([^`]+)`"""), "$1")
            // Trim extra whitespace
            .trim()
    }
    suspend fun getMultiTurnConversationChat(chats: List<Pair<String, String>>): String {
        return repository.getMultiTurnConversationChat(chats)
            // Remove bold/italic asterisks but keep the text inside: **text** -> text
            .replace(Regex("""\*+(.*?)\*+"""), "$1")
            // Remove bold/italic underscores: __text__ -> text
            .replace(Regex("""_+(.*?)_+"""), "$1")
            // Remove markdown headers: # Header -> Header
            .replace(Regex("""^#+\s+""", RegexOption.MULTILINE), "")
            // Remove inline code backticks: `code` -> code
            .replace(Regex("""`([^`]+)`"""), "$1")
            // Trim extra whitespace
            .trim()
    }
}