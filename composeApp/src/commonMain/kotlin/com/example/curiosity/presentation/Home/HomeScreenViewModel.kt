package com.example.curiosity.presentation.Home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.curiosity.core.models.PageData
import com.example.curiosity.core.models.ResultData
import com.example.curiosity.core.models.StatusTypes
import com.example.curiosity.core.models.UiStates
import com.example.curiosity.domain.models.CrawlResponseData
import com.example.curiosity.domain.models.IndexResponseData
import com.example.curiosity.domain.usecase.CrawlResultsUseCase
import com.example.curiosity.domain.usecase.GemmaUseCase
import com.example.curiosity.domain.usecase.IndexResultsUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class HomeScreenViewModel(
    private val getIndexResultsUseCase: IndexResultsUseCase,
    private val getCrawlResultsUseCase: CrawlResultsUseCase,
    private val gemmaUseCase: GemmaUseCase
): ViewModel() {

    private val _widthState = MutableStateFlow(0)
    val widthState = _widthState as StateFlow<Int>

    fun onWidthChange(newWidth: Int){
        _widthState.value = newWidth
    }

    private val _currentFullScreenState = MutableStateFlow(false)
    val currentFullScreenState = _currentFullScreenState as StateFlow<Boolean>

    fun onSearchAndNavigateToFullView(query: String){
        _currentFullScreenState.value = true
        onSearchStarted(query)
    }

    private val _currentSearchQueryState = MutableStateFlow("")
    val currentSearchQueryState = _currentSearchQueryState as StateFlow<String>

    fun onSearchQueryChange(newQuery: String) {
        _currentSearchQueryState.value = newQuery
    }

    private val _searchStartedState = MutableStateFlow(false)
    val searchStartedState = _searchStartedState as StateFlow<Boolean>

    private val _currentSearchingState = MutableStateFlow(false)
    val currentSearchingState = _currentSearchingState as StateFlow<Boolean>

    private val _uiState = MutableStateFlow<UiStates>(UiStates.InitialState)
    val uiState = _uiState as StateFlow<UiStates>

    fun onSearchStarted(query: String) {
        _uiState.value = UiStates.InitialState
        disableWebView()
        // Web page search
        if (query.trim().startsWith("https://")
            || query.trim().startsWith("http://")){
            updateUrl(query.trim())
            onWebPageLoading()
            enableWebView()
        }
        // Query based search
        else{
            _currentSearchingState.value = true
            viewModelScope.launch {
                supervisorScope {
                    val indexJob = async {
                        runCatching {
                            getIndexResultsUseCase(query)
                        }
                    }
                    val crawlJob = async {
                        runCatching {
                            getCrawlResultsUseCase(query)
                        }
                    }
                    val titleJob = async {
                        runCatching {
                            gemmaUseCase.getSearchTitle(query)
                        }
                    }
                    val suggestionJob = async {
                        runCatching {
                            gemmaUseCase.getSearchSuggestion(query)
                        }
                    }
                    // INDEXER RESULTS
                    launch {
                        try {
                            indexJob.await()
                                .onSuccess { indexResults ->
                                    if (_uiState.value is UiStates.InitialState) {
                                        when (indexResults.status_type) {
                                            StatusTypes.GOOGLE_API_ERROR -> {
                                                _uiState.value = UiStates.IndexerError("Agentic pipeline error")
                                            }
                                            StatusTypes.INDEX_DB_ERROR -> {
                                                _uiState.value = UiStates.IndexerError("Index database error")
                                            }
                                            StatusTypes.UNMATCHED_INDEXED_RESULTS -> {
                                                _uiState.value = UiStates.EmptyResults("No results found")
                                            }
                                            StatusTypes.MATCHED_INDEXED_RESULTS -> {
                                                // APPEND INDEXER RESULTS TO LOCAL MODEL
                                                appendIndexResults(query, indexResults)

                                                _uiState.value = UiStates.IndexResults(indexResults)
                                                if (!_searchStartedState.value){
                                                    _searchStartedState.value = true
                                                }
                                                enableResultsCanvas(query)
                                            }
                                            else -> {
                                                _uiState.value = UiStates.IndexerError("Something went wrong")
                                            }
                                        }
                                    }
                                }
                                .onFailure { e ->
                                    println("<<<ERROR WHILE MAKING INDEXER REQUEST OR DECODING RESPONSE:\n$e>>>")
                                    _uiState.value = UiStates.IndexerError("Something went wrong")
                                }
                        }
                        catch (e: Exception) {
                            println("<<<ERROR WHILE MAKING INDEXER REQUESTS:\n$e>>>")
                        }
                    }
                    // CRAWLER RESULTS
                    launch {
                        try {
                            crawlJob.await()
                                .onSuccess {
                                    it.transformWhile { crawlResults ->
                                        emit(crawlResults)
                                        crawlResults.status_type == StatusTypes.CRAWLING_IN_PROCESS
                                    }.collect { crawlResults ->
                                        when (crawlResults.status_type) {
                                            StatusTypes.GOOGLE_API_ERROR -> {
                                                _uiState.value = UiStates.CrawlerError("Agentic pipeline error")
                                            }
                                            StatusTypes.CRAWLING_IN_PROCESS -> {}
                                            StatusTypes.CRAWLING_FINISHED -> {
                                                // APPEND CRAWLER RESULTS TO LOCAL MODEL
                                                _currentSearchingState.value = false
                                                appendCrawlResults(query, crawlResults)

                                                _uiState.value = UiStates.CrawlResults(crawlResults)

                                                if (!_showResultsCanvasState.value){
                                                    enableResultsCanvas(query)
                                                }
                                                // GENERATE SEARCH TITLE
                                                titleJob.await()
                                                    .onSuccess { title ->
                                                        println("<<<TITLE: $title>>>")
                                                        if (!_searchStartedState.value){
                                                            onSearchHeadingChange(title)
                                                        }
                                                    }
                                                    .onFailure {
                                                        if (!_searchStartedState.value){
                                                            onSearchHeadingChange(query)
                                                        }
                                                    }
                                                if (!_searchStartedState.value){
                                                    _searchStartedState.value = true
                                                }
                                                // GENERATE SEARCH SUGGESTION CURIOUS QUESTION
                                                suggestionJob.await()
                                                    .onSuccess { suggestionQuestion ->
                                                        onUpdateCurrentCuriousQuestion(suggestionQuestion)
                                                        enableCurrentCuriousQuestionBox()
                                                    }
                                                // GENERATE SEARCH SUGGESTION CURIOUS QUESTION TOPIC
                                                async {
                                                    runCatching {
                                                        gemmaUseCase.getSearchTitle(_searchCurrentCuriousQuestionState.value)
                                                    }
                                                }.await()
                                                    .onSuccess { suggestionTopic ->
                                                        onUpdateCurrentCuriousQuestionTopicHeading(suggestionTopic)
                                                    }
                                            }
                                            StatusTypes.CRAWLING_FAILED -> {
                                                _uiState.value = UiStates.CrawlerError("Couldn't crawl the web")
                                            }
                                            else -> {
                                                _uiState.value = UiStates.CrawlerError("Something went wrong")
                                            }
                                        }
                                    }
                                }
                                .onFailure {}
                        }
                        catch (e: Exception) {
                            println("<<<ERROR WHILE MAKING CRAWLER REQUESTS:\n$e>>>")
                            if (_uiState.value is UiStates.EmptyResults || _uiState.value is UiStates.IndexerError){
                                _uiState.value = UiStates.CrawlerError("Something went wrong")
                            }
                        }
                        finally {
                            _currentSearchingState.value = false
                        }
                    }
                }
            }
        }
    }

    private val _searchHeadingTextState = MutableStateFlow("New search")
    val searchHeadingTextState = _searchHeadingTextState as StateFlow<String>

    fun onSearchHeadingChange(newText: String){
        _searchHeadingTextState.value = newText
    }

    private val _searchCurrentCuriousQuestionBoxState = MutableStateFlow(false)
    val searchCurrentCuriousQuestionBoxState = _searchCurrentCuriousQuestionBoxState as StateFlow<Boolean>

    fun enableCurrentCuriousQuestionBox(){
        _searchCurrentCuriousQuestionBoxState.value = true
    }

    fun disableCurrentCuriousQuestionBox(){
        _searchCurrentCuriousQuestionBoxState.value = false
    }

    private val _searchCurrentCuriousQuestionState = MutableStateFlow("")
    val searchCurrentCuriousQuestionState = _searchCurrentCuriousQuestionState as StateFlow<String>

    fun onUpdateCurrentCuriousQuestion(newQuestion: String){
        _searchCurrentCuriousQuestionState.value = newQuestion
    }

    private val _searchCurrentCuriousQuestionTopicHeadingState = MutableStateFlow("")
    val searchCurrentCuriousQuestionTopicHeadingState = _searchCurrentCuriousQuestionTopicHeadingState as StateFlow<String>

    fun onUpdateCurrentCuriousQuestionTopicHeading(newHeading: String){
        _searchCurrentCuriousQuestionTopicHeadingState.value = newHeading
    }

    private val _webPageLoadingStatus = MutableStateFlow(false)
    val webPageLoadingStatus = _webPageLoadingStatus as StateFlow<Boolean>

    fun onWebPageLoading(){
        _webPageLoadingStatus.value = true
    }

    fun onWebPageLoaded(){
        _webPageLoadingStatus.value = false
    }

    private val _showWebViewState = MutableStateFlow(false)
    val showWebViewState = _showWebViewState as StateFlow<Boolean>

    fun enableWebView(){
        _showWebViewState.value = true
    }

    fun disableWebView(){
        _showWebViewState.value = false
        _webPageLoadingStatus.value = false
    }

    // OPEN SOURCE PAGE
    fun openSourcePage(url: String){
        updateUrl(url.trim())
        onWebPageLoading()
        enableWebView()
    }

    private val _processedUrlState = MutableStateFlow("")
    val processedUrlState = _processedUrlState as StateFlow<String>

    fun updateUrl(newUrl: String){
        _processedUrlState.value = newUrl
    }

    // RESULTS AND QUERIES CANVAS

    private val _searchQueriesAndResults = MutableStateFlow<List<Pair<String, ResultData>>>(emptyList())
    val searchQueriesAndResults = _searchQueriesAndResults as StateFlow<List<Pair<String, ResultData>>>

    fun appendIndexResults(query: String, indexResults: IndexResponseData) {
        val pages = indexResults.indexed_results.map { result ->
            PageData(
                pageTitle = result.payload.source_metadata.title,
                pageUrl = result.payload.source_metadata.url,
                pageSummary = result.payload.source_metadata.summary,
                pageDate = result.payload.source_metadata.date,
                pageCredibilityReason = result.payload.source_metadata.credibility_reason
            )
        }

        val  firstResult = indexResults.indexed_results.firstOrNull()?.payload?.results_metadata

        val newResultData = ResultData(
            resultSummary = firstResult?.overall_answer_and_summary ?: "",
            resultPages = pages,
            resultFacts = firstResult?.facts ?: emptyList(),
            resultFactRelationships = firstResult?.fact_relationships ?: emptyList()
        )

        _searchQueriesAndResults.update { currentList ->
            currentList + (query to newResultData)
        }
    }

    fun appendCrawlResults(query: String, crawlResults: CrawlResponseData) {
        val incomingPages = crawlResults.crawled_results.detailed_search_results.map { detailed ->
            PageData(
                pageTitle = detailed.title,
                pageUrl = detailed.url,
                pageSummary = detailed.summary,
                pageDate = detailed.date,
                pageCredibilityReason = detailed.credibility_reason
            )
        }

        _searchQueriesAndResults.update { currentList ->
            val newList = currentList.toMutableList()
            val index = newList.indexOfFirst { it.first == query }

            if (index != -1) {
                val existing = newList[index].second

                val mergedPages = (existing.resultPages + incomingPages)
                    .associateBy { it.pageUrl }
                    .values.toList()

                val mergedFacts = (existing.resultFacts + crawlResults.crawled_results.facts)
                    .associateBy { it.claim }
                    .values.toList()

                val mergedRelationships = (existing.resultFactRelationships + crawlResults.crawled_results.fact_relationships)
                    .associateBy { "${it.source_claim}|${it.target_claim}" }
                    .values.toList()

                newList[index] = query to ResultData(
                    resultSummary = crawlResults.crawled_results.overall_answer_and_summary,
                    resultPages = mergedPages,
                    resultFacts = mergedFacts,
                    resultFactRelationships = mergedRelationships
                )
            } else {
                newList.add(query to ResultData(
                    resultSummary = crawlResults.crawled_results.overall_answer_and_summary,
                    resultPages = incomingPages,
                    resultFacts = crawlResults.crawled_results.facts,
                    resultFactRelationships = crawlResults.crawled_results.fact_relationships
                ))
            }
            newList
        }
    }


    private val _showResultsCanvasState = MutableStateFlow(false)
    val showResultsCanvasState = _showResultsCanvasState as StateFlow<Boolean>

    fun enableResultsCanvas(query: String){
        _showResultsCanvasState.value = true
        _currentSearchQueryState.value = query
    }

    fun disableResultsCanvas(){
        _showResultsCanvasState.value = false
    }

}