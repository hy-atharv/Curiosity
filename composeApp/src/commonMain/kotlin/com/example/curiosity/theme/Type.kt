package com.example.curiosity.theme

import androidx.compose.runtime.Composable
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import curiosity.composeapp.generated.resources.Res
import curiosity.composeapp.generated.resources.segoe_ui_variable


val Typography: Typography @Composable get() = Typography(
    bodyLarge = TextStyle( // For user/AI message text
        fontFamily = FontFamily(Font(Res.font.segoe_ui_variable)),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle( // For chat input field
        fontFamily = FontFamily(Font(Res.font.segoe_ui_variable)),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    titleMedium = TextStyle( // For app bar titles, user names
        fontFamily = FontFamily(Font(Res.font.segoe_ui_variable)),
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily(Font(Res.font.segoe_ui_variable)),
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle( // For timestamps, small metadata
        fontFamily = FontFamily(Font(Res.font.segoe_ui_variable)),
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle( // For buttons (e.g., Send)
        fontFamily = FontFamily(Font(Res.font.segoe_ui_variable)),
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    headlineMedium = TextStyle( // Standard MD3 size for section headers
        fontFamily = FontFamily(Font(Res.font.segoe_ui_variable)),
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily(Font(Res.font.segoe_ui_variable)),
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    displayLarge = TextStyle(
        fontFamily = FontFamily(Font(Res.font.segoe_ui_variable)),
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily(Font(Res.font.segoe_ui_variable)),
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    )
)