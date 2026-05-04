package com.example.curiosity.presentation.DiscoveryChat

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.curiosity.domain.usecase.GemmaUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchDiscoveryChatScreenViewModel(
    private val gemmaUseCase: GemmaUseCase
): ViewModel() {
    private val _widthState = MutableStateFlow(0)
    val widthState = _widthState as StateFlow<Int>

    fun onWidthChange(newWidth: Int){
        _widthState.value = newWidth
    }

    private val _currentResponseLoadingState = MutableStateFlow(false)
    val currentResponseLoadingState = _currentResponseLoadingState as StateFlow<Boolean>

    private val _currentDiscoveryChats = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val currentDiscoveryChats = _currentDiscoveryChats as StateFlow<List<Pair<String, String>>> // [..<Role, Chat>]

    val scrollEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    fun loadDiscoveryChats(chats: List<Pair<String, String>>){
        _currentDiscoveryChats.value = chats
    }

    fun resetDiscoveryChats(){
        _currentDiscoveryChats.value = emptyList()
    }

    fun onMessageQuerySent(messageQuery: String) {
        _currentDiscoveryChats.value += "user" to messageQuery
        scrollEvents.tryEmit(Unit)
        _currentResponseLoadingState.value = true

        viewModelScope.launch {
            val modelChatJob = async {
                runCatching {
                    gemmaUseCase.getMultiTurnConversationChat(_currentDiscoveryChats.value)
                }
            }
            modelChatJob.await()
                .onSuccess { modelChat ->
                    _currentDiscoveryChats.value += "model" to modelChat
                }
                .onFailure { error ->
                    println("ERROR: $error")
                    _currentDiscoveryChats.value += "model" to "Something went wrong"
                }
            scrollEvents.tryEmit(Unit)
            _currentResponseLoadingState.value = false
        }
    }
}