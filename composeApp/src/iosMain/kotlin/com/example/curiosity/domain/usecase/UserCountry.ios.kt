package com.example.curiosity.domain.usecase

import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleCountryCode
import platform.Foundation.currentLocale

actual fun getUserCountryCode(): String {
    return NSLocale.currentLocale.objectForKey(NSLocaleCountryCode) as? String ?: "US"
}