package com.example.curiosity.domain.usecase

actual fun getUserCountryCode(): String {
    return js("Intl.DateTimeFormat().resolvedOptions().region") as? String ?: "US"
}