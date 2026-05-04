package com.example.curiosity.di

import com.example.curiosity.core.models.CurrentUserSessionDataViewModel
import com.example.curiosity.data.remote.CrawlerApiService
import com.example.curiosity.data.remote.CrawlerApiServiceImpl
import com.example.curiosity.data.remote.GemmaApiService
import com.example.curiosity.data.remote.GemmaApiServiceImpl
import com.example.curiosity.data.remote.IndexerApiService
import com.example.curiosity.data.remote.IndexerApiServiceImpl
import com.example.curiosity.data.repository.CrawlRepositoryImpl
import com.example.curiosity.data.repository.GemmaRepositoryImpl
import com.example.curiosity.data.repository.IndexRepositoryImpl
import com.example.curiosity.domain.repository.CrawlRepository
import com.example.curiosity.domain.repository.GemmaRepository
import com.example.curiosity.domain.repository.IndexRepository
import com.example.curiosity.domain.usecase.CrawlResultsUseCase
import com.example.curiosity.domain.usecase.GemmaUseCase
import com.example.curiosity.domain.usecase.IndexResultsUseCase
import com.example.curiosity.presentation.DiscoveryChat.SearchDiscoveryChatScreenViewModel
import com.example.curiosity.presentation.Home.HomeScreenViewModel
import com.example.curiosity.presentation.NewSearch.NewSearchScreenViewModel
import com.example.curiosity.presentation.RecentSearch.RecentSearchScreenViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val repositorySharedModule = module {
    single<IndexRepository> { IndexRepositoryImpl(get()) }
    single<CrawlRepository> { CrawlRepositoryImpl(get()) }
    single<GemmaRepository> { GemmaRepositoryImpl(get()) }
}

val useCaseSharedModule = module {
    factory { IndexResultsUseCase(get()) }
    factory { CrawlResultsUseCase(get()) }
    factory { GemmaUseCase(get()) }
}

val viewModelsSharedModule = module {
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::NewSearchScreenViewModel)
    viewModelOf(::CurrentUserSessionDataViewModel)
    viewModelOf(::RecentSearchScreenViewModel)
    viewModelOf(::SearchDiscoveryChatScreenViewModel)
}

val networkSharedModule = module {
    single<HttpClient> {
        createHttpClient()
    }
    single<IndexerApiService> { IndexerApiServiceImpl(get()) }
    single<CrawlerApiService> { CrawlerApiServiceImpl(get()) }
    single<GemmaApiService> { GemmaApiServiceImpl(get()) }
}

expect fun createHttpClient(): HttpClient

expect val platformModule: Module