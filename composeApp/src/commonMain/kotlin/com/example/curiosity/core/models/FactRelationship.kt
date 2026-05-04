package com.example.curiosity.core.models

import kotlinx.serialization.Serializable

@Serializable
data class FactRelationship(
    val connection_type: FactRelationshipTypes,
    val source_claim: String,
    val target_claim: String
)
