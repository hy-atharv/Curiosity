package com.example.curiosity.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.timeout
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

class GemmaApiServiceImpl(private val client: HttpClient): GemmaApiService {
    val gemmaEndpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"

    override suspend fun generateSearchTitle(query: String): String {
        val responseText = client.post(
            urlString = gemmaEndpoint,
            block = {
                contentType(ContentType.Application.Json)
                header("x-goog-api-key", googleApiKey)
                timeout {
                    socketTimeoutMillis = 15_000
                    requestTimeoutMillis = 15_000
                    connectTimeoutMillis = 30_000
                }
                setBody(
                    buildJsonObject {
                        putJsonArray("contents") {
                            addJsonObject {
                                putJsonArray("parts") {
                                    addJsonObject {
                                        put("text", query)
                                    }
                                }
                            }
                        }

                        putJsonObject("systemInstruction") {
                            putJsonArray("parts") {
                                addJsonObject {
                                    put(
                                        "text",
                                        "Task: Generate a short title for the following query. " +
                                                "Rules: Output ONLY the title. No quotes, no bolding, no intro text, no conversational filler. " +
                                                "If you explain the title, you have failed the task."
                                    )
                                }
                            }
                        }

                        putJsonObject("generationConfig") {
                            put("temperature", 0.0)
                            put("maxOutputTokens", 20)
                            putJsonArray("stopSequences") {
                                add("\n")
                            }
                            putJsonObject("thinkingConfig") {
                                put("include_thoughts", false)
                                put("thinking_level", "minimal")
                            }
                        }
                    }
                )
            }
        ).bodyAsText()
        val responseJson = Json.parseToJsonElement(responseText)
        val title = responseJson.jsonObject["candidates"]
                ?.jsonArray
                ?.getOrNull(0)
                ?.jsonObject
                ?.get("content")
                ?.jsonObject
                ?.get("parts")
                ?.jsonArray
                ?.getOrNull(1)
                ?.jsonObject
                ?.get("text")
                ?.jsonPrimitive
                ?.content
                ?.trim()
                .orEmpty()
        return title
    }

    override suspend fun generateSearchSuggestion(query: String): String {
        val responseText = client.post(
            urlString = gemmaEndpoint,
            block = {
                contentType(ContentType.Application.Json)
                timeout {
                    socketTimeoutMillis = 15_000
                    requestTimeoutMillis = 15_000
                    connectTimeoutMillis = 30_000
                }
                header("x-goog-api-key", googleApiKey)
                setBody(
                    buildJsonObject {
                        putJsonArray("contents") {
                            addJsonObject {
                                putJsonArray("parts") {
                                    addJsonObject {
                                        put("text", query)
                                    }
                                }
                            }
                        }

                        putJsonObject("systemInstruction") {
                            putJsonArray("parts") {
                                addJsonObject {
                                    put(
                                        "text",
                                        "Task: Based on the user query, provide a single, highly probable follow-up search suggestion in the form of a curious question. " +
                                                "Rules: Be concise. Use a curious tone. Output only the plain text without any markdown or formatting. No intro, no quotes."
                                    )
                                }
                            }
                        }

                        putJsonObject("generationConfig") {
                            put("temperature", 0.7)
                            put("maxOutputTokens", 40)
                            putJsonArray("stopSequences") {
                                add("\n")
                            }
                            putJsonObject("thinkingConfig") {
                                put("include_thoughts", false)
                                put("thinking_level", "minimal")
                            }
                        }
                    }
                )
            }
        ).bodyAsText()
        val responseJson = Json.parseToJsonElement(responseText)
        val suggestion = responseJson.jsonObject["candidates"]
            ?.jsonArray
            ?.getOrNull(0)
            ?.jsonObject
            ?.get("content")
            ?.jsonObject
            ?.get("parts")
            ?.jsonArray
            ?.getOrNull(1)
            ?.jsonObject
            ?.get("text")
            ?.jsonPrimitive
            ?.content
            ?.trim()
            .orEmpty()
        return suggestion
    }

    override suspend fun generateMultiTurnConversationChat(chats: List<Pair<String, String>>): String {
        val responseText = client.post(
            urlString = gemmaEndpoint,
            block = {
                contentType(ContentType.Application.Json)
                timeout {
                    socketTimeoutMillis = 30_000
                    requestTimeoutMillis = 30_000
                    connectTimeoutMillis = 30_000
                }
                header("x-goog-api-key", googleApiKey)
                setBody(
                    buildJsonObject {
                        putJsonArray("contents") {
                            chats.forEach { (role, message) ->
                                addJsonObject {
                                    put("role", if (role == "user") "user" else "model")
                                    putJsonArray("parts") {
                                        addJsonObject {
                                            put("text", message)
                                        }
                                    }
                                }
                            }
                        }

                        putJsonObject("systemInstruction") {
                            putJsonArray("parts") {
                                addJsonObject {
                                    put(
                                        "text",
                                        "You are a Search and Discovery Assistant. " +
                                                "Rules: 1. Reply using ONLY plain text without markdown or symbols. " +
                                                "2. Use the search tool ONLY for real-time data, current events, or information " +
                                                "past your last training data. 3. If the query can be answered with your " +
                                                "internal knowledge, do not trigger a search."
                                    )
                                }
                            }
                        }

                        putJsonArray("tools") {
                            addJsonObject {
                                putJsonObject("google_search") { }
                            }
                        }

                        putJsonObject("generationConfig") {
                            put("maxOutputTokens", 100)
                            putJsonObject("thinkingConfig") {
                                put("include_thoughts", false)
                                put("thinking_level", "minimal")
                            }
                        }
                    }
                )
            }
        ).bodyAsText()
        val responseJson = Json.parseToJsonElement(responseText)
        val modelChat = responseJson.jsonObject["candidates"]
            ?.jsonArray
            ?.getOrNull(0)
            ?.jsonObject
            ?.get("content")
            ?.jsonObject
            ?.get("parts")
            ?.jsonArray?.firstOrNull { it is JsonObject && it.size == 1 && it.containsKey("text") }
            ?.jsonObject
            ?.get("text")
            ?.jsonPrimitive
            ?.content
            ?.trim()
            .orEmpty()
        return modelChat
    }
}