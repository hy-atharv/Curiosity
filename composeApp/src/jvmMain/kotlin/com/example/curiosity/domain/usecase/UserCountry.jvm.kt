package com.example.curiosity.domain.usecase

actual fun getUserCountryCode(): String {
    return java.util.Locale.getDefault().country
}