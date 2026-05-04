package com.example.curiosity.core.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeSearchAndResultsScreen


@Serializable
data object NewSearchAndResultsScreen


@Serializable
data object RecentSearchAndResultsScreen


@Serializable
data class SearchAndDiscoveryChatScreen(
    var searchId: Int,
    var questionTopic: String,
    var questionQuery: String
)

