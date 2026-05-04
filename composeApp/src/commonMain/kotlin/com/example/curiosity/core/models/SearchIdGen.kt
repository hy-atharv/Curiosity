package com.example.curiosity.core.models

import kotlin.random.Random

object Rng {
    private val rng = Random

    fun generateSearchId(): Int {
        return rng.nextInt()
    }
}