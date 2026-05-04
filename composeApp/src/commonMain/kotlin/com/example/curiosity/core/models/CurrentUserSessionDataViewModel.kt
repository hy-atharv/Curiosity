package com.example.curiosity.core.models

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CurrentUserSessionDataViewModel: ViewModel() {

    private val _searchDataMap = MutableStateFlow(emptyMap<Int, SearchData>())
    val searchDataMap = _searchDataMap as StateFlow<Map<Int, SearchData>>

    private val _recentSearches = MutableStateFlow(emptyList<Pair<Int, String>>())
    val recentSearches = _recentSearches as StateFlow<List<Pair<Int, String>>>

    fun insertQueryAndResultDataToSearchData(searchId: Int, newQuery: String, newResult: ResultData){
        _searchDataMap.value[searchId]?.let {
            val newSearchData = it.copy(
                searchAllQueriesAndResults = it.searchAllQueriesAndResults + (newQuery to newResult)
            )
            _searchDataMap.value += (searchId to newSearchData)
        }
    }

    fun renameSearchData(searchId: Int, newSearchHeading: String){
        _searchDataMap.value[searchId]?.let {
            val newSearchData = it.copy(
                searchHeading = newSearchHeading
            )
            _searchDataMap.value += (searchId to newSearchData)
        }
        _recentSearches.value.forEachIndexed { index, pair ->
            if (pair.first == searchId){
                _recentSearches.value = _recentSearches.value.toMutableList().apply {
                    this[index] = pair.copy(second = newSearchHeading)
                }
            }
        }
        _searchCuriosityAndDiscoveryDataMap.value[searchId]?.let {
            val newSearchData = it.copy(
                searchHeading = newSearchHeading
            )
            _searchCuriosityAndDiscoveryDataMap.value += (searchId to newSearchData)
        }
    }

    fun insertSearchDataToSearchDataMap(newSearchData: SearchData){
        _searchDataMap.value += (newSearchData.searchId to newSearchData)
        _recentSearches.value += (newSearchData.searchId to newSearchData.searchHeading)
    }

    fun deleteSearchData(searchId: Int){
        _recentSearches.value = _recentSearches.value.filter { it.first != searchId }
        _searchDataMap.value -= searchId
        _searchCuriosityAndDiscoveryDataMap.value -= searchId
    }


    private val _searchCuriosityAndDiscoveryDataMap = MutableStateFlow(emptyMap<Int, SearchCuriosityAndDiscoveryData>())
    val searchCuriosityAndDiscoveryDataMap = _searchCuriosityAndDiscoveryDataMap as StateFlow<Map<Int, SearchCuriosityAndDiscoveryData>>

    fun insertSearchCuriosityAndDiscoveryDataToMap(newData: SearchCuriosityAndDiscoveryData){
        _searchCuriosityAndDiscoveryDataMap.value += (newData.searchId to newData)
    }

    fun insertSearchDiscoveryChat(searchId: Int, newChat: String, chatRole: String){
        _searchCuriosityAndDiscoveryDataMap.value[searchId]?.let {
            val newSearchData = it.copy(
                searchDiscoveryChats = it.searchDiscoveryChats + Pair(chatRole, newChat)
            )
            _searchCuriosityAndDiscoveryDataMap.value += (searchId to newSearchData)
        }
    }

    private val _renamePopUpState = MutableStateFlow(false)
    val renamePopUpState = _renamePopUpState as StateFlow<Boolean>

    private val _renamePopUpData = MutableStateFlow(RenamePopUpData())
    val renamePopUpData = _renamePopUpData as StateFlow<RenamePopUpData>


    fun enableRenamePopUp(data: RenamePopUpData){
        _renamePopUpData.value = data
        _renamePopUpState.value = true
    }

    fun disableRenamePopUp(){
        _renamePopUpState.value = false
    }

    private val _searchOptionsPopUpState = MutableStateFlow(false)
    val searchOptionsPopUpState = _searchOptionsPopUpState as StateFlow<Boolean>

    fun toggleSearchOptionsPopUp(){
        _searchOptionsPopUpState.value = !_searchOptionsPopUpState.value
    }

    private val _searchOptionsPopUpIdState = MutableStateFlow(0)
    val searchOptionsPopUpIdState = _searchOptionsPopUpIdState as StateFlow<Int>

    fun switchSearchOptionsPopUpId(id: Int){
        _searchOptionsPopUpIdState.value = id
    }

}

data class RenamePopUpData(
    var oldHeading: String = "",
    var searchId: Int = 0
)