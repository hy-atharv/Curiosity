package com.example.curiosity.data.remote

import com.example.curiosity.data.models.IndexResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.plugins.timeout
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class IndexerApiServiceImpl(private val client: HttpClient) : IndexerApiService {
    override suspend fun indexResults(query: String, country: String): IndexResponseDto {
        val responseText = client.post(
            urlString = "http://192.168.145.141:8080/indexResults",
            block = {
                timeout {
                    socketTimeoutMillis = 60_000
                    requestTimeoutMillis = 60_000
                    connectTimeoutMillis = 60_000
                }
                setBody(
                    buildJsonObject {
                        put("query", query)
                        put("country", country)
                    }
                )
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
        ).bodyAsText()
        println("<<DATA_INDEXED>>:\n$responseText")
        return Json.decodeFromString<IndexResponseDto>(responseText)
    }
}