package com.example.curiosity.core.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeScreen

@Serializable
data class NewSearchScreen(
    var searchId: Int,
)

@Serializable
data class RecentSearchScreen(
    var searchId: Int,
)

