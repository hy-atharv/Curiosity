package com.example.curiosity.core.models

data class SearchData(
    var searchId: Int,
    var searchHeading: String,
    var searchInitialQuery: String,
    var searchAllQueriesAndResults: List<Pair<String, ResultData>> // <Query, ResultData>
)

data class ResultData(
    var resultSummary: String,
    var resultPages: List<PageData>,
    var resultFacts: List<Fact>,
    var resultFactRelationships: List<FactRelationship>
)

data class PageData(
    var pageTitle: String,
    var pageUrl: String,
    var pageSummary: String,
    var pageDate: String,
    var pageCredibilityReason: String
)

data class SearchCuriosityAndDiscoveryData(
    var searchId: Int,
    var searchHeading: String,
    var searchInitialQuery: String,
    var searchAllQueriesAndResults: List<Pair<String, ResultData>>,
    var searchLatestQuestionSuggestion: String,
    var searchLatestQuestionSuggestionTopic: String,
    var searchDiscoveryChats: List<Pair<String, String>> // [..<Role, Chat>]
)
