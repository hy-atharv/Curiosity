package com.example.curiosity.di


import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.sse.SSE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

actual val platformModule = module {

}

actual fun createHttpClient(): HttpClient {
    return HttpClient{
        install(ContentNegotiation){
            json(
                Json { ignoreUnknownKeys = true }
            )
        }
        install(SSE)
    }
}