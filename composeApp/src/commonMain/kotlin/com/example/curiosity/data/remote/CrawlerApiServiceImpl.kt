package com.example.curiosity.data.remote



import com.example.curiosity.core.models.StatusTypes
import com.example.curiosity.data.models.CrawlResponseDto
import io.ktor.client.plugins.sse.sse
import kotlinx.coroutines.flow.Flow
import io.ktor.client.HttpClient
import io.ktor.client.plugins.timeout
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray


class CrawlerApiServiceImpl(private val client: HttpClient) : CrawlerApiService {
    override fun streamCrawledResults(query: String, country: String): Flow<CrawlResponseDto> =
        channelFlow {
            client.sse(
                urlString = "http://192.168.145.141:8080/crawlResults",
                request = {
                    url {
                        parameters.append("query", query)
                        parameters.append("country", country)
                    }
                    timeout {
                        socketTimeoutMillis = 60_000
                        requestTimeoutMillis = 60_000
                        connectTimeoutMillis = 60_000
                    }
                }
            ) {
                println("SSE Session Started")
                try {
                    incoming.takeWhile { event ->
                        val data = event.data
                        println("<<<DATA:>>>\n$data")
                        data?.let {
                            var decoded = Json.decodeFromString<CrawlResponseDto>(it)
                            if (decoded.crawled_results is JsonNull){
                                decoded = decoded.copy(
                                    crawled_results = buildJsonObject {
                                        put("overall_answer_and_summary", "")
                                        putJsonArray("detailed_search_results") {}
                                        putJsonArray("facts") {}
                                        putJsonArray("fact_relationships") {}
                                    }
                                )
                            }
                            send(decoded)
                            decoded.status_type == StatusTypes.CRAWLING_IN_PROCESS
                        } ?: true
                    }.collect()
                }
                catch (e: Exception){
                    println("<<<ERROR WHILE COLLECTING SSE EVENTS:\n$e>>>")
                    close(e)
                }
            }
        }
}