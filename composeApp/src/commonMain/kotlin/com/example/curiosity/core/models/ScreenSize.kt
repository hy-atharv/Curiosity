package com.example.curiosity.core.models

import androidx.compose.ui.text.font.FontVariation.width

enum class ScreenSizeState {
    COMPACT,
    MEDIUM,
    EXPANDED;

    companion object {
        fun fromWidth(width: Int): ScreenSizeState {
            return when {
                width < 600 -> COMPACT
                width in 600..840 -> MEDIUM
                else -> EXPANDED
            }
        }
    }
}
