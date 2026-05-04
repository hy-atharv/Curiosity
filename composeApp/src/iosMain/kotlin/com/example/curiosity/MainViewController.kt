package com.example.curiosity

import androidx.compose.ui.window.ComposeUIViewController
import com.example.curiosity.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    App()
}