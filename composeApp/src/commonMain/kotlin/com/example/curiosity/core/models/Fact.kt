package com.example.curiosity.core.models

import kotlinx.serialization.Serializable

@Serializable
data class Fact(
    val claim: String,
    val evidence_urls: List<String>
)
