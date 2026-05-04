package com.example.curiosity.presentation.RecentSearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.curiosity.domain.usecase.CrawlResultsUseCase
import com.example.curiosity.domain.usecase.GemmaUseCase
import com.example.curiosity.domain.usecase.IndexResultsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecentSearchScreenViewModel(
    private val getIndexResultsUseCase: IndexResultsUseCase,
    private val getCrawlResultsUseCase: CrawlResultsUseCase,
    private val gemmaUseCase: GemmaUseCase
): ViewModel() {
    private val _widthState = MutableStateFlow(0)
    val widthState = _widthState as StateFlow<Int>

    fun onWidthChange(newWidth: Int){
        _widthState.value = newWidth
    }

    private val _currentSearchQueryState = MutableStateFlow("")
    val currentSearchQueryState = _currentSearchQueryState as StateFlow<String>

    fun onSearchQueryChange(newQuery: String) {
        _currentSearchQueryState.value = newQuery
    }

    private val _currentSearchingState = MutableStateFlow(false)
    val currentSearchingState = _currentSearchingState as StateFlow<Boolean>

    fun onSearchStarted(query: String) {
        disableWebView()
        if (query.trim().startsWith("https://")
            || query.trim().startsWith("http://")){
            updateUrl(query.trim())
            onWebPageLoading()
            enableWebView()
        }
        else{
            _currentSearchingState.value = true
            viewModelScope.launch {
                delay(5000L)
                _currentSearchingState.value = false
                _searchCurrentCuriousQuestionBoxState.value = true
            }
        }
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

    private val _processedUrlState = MutableStateFlow("")
    val processedUrlState = _processedUrlState as StateFlow<String>

    fun updateUrl(newUrl: String){
        _processedUrlState.value = newUrl
    }
}