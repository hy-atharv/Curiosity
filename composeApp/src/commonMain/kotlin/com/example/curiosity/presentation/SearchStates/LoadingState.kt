package com.example.curiosity.presentation.SearchStates

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.bottomAnimatedTaperedGradientBorder(
    baseColors: List<Color>,
    thickness: Dp = 2.dp,
    animationDuration: Int = 1000
) = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "gemini_border_transition")

    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animationDuration,
                easing = LinearEasing // Constant speed for a "rotating" look
            ),
            repeatMode = RepeatMode.Restart // Single direction sweep
        ),
        label = "gradient_sweep_progress"
    )

    // The gradient definition: transparent ends, solid colors in the middle
    val taperedColors = remember(baseColors) {
        listOf(
            Color.Transparent, // 0%
            baseColors[0],     // 30%
            baseColors[1],     // 50%
            baseColors[2],     // 70%
            Color.Transparent  // 100%
        )
    }

    drawBehind {
        val strokeWidthPx = thickness.toPx()
        val yPosition = size.height - strokeWidthPx / 2f

        // The brush needs to be very wide relative to the container to simulate the smooth sweep
        val brushWidth = size.width * 3f

        // Calculate the starting X offset. We move the entire wide brush from
        // completely off the left screen to completely off the right screen.
        val startX = -brushWidth + (size.width + brushWidth) * animationProgress

        val brush = Brush.linearGradient(
            colors = taperedColors,
            start = Offset(startX, yPosition),
            end = Offset(startX + brushWidth, yPosition),
            tileMode = TileMode.Clamp
        )

        // Draw the line segment that is visible within the bounds of your TextField
        drawLine(
            brush = brush,
            start = Offset(0f, yPosition),
            end = Offset(size.width, yPosition),
            strokeWidth = strokeWidthPx
        )
    }
}

fun Modifier.topAnimatedTaperedGradientBorder(
    baseColors: List<Color>,
    thickness: Dp = 2.dp,
    animationDuration: Int = 1000
) = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "gemini_border_transition")

    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animationDuration,
                easing = LinearEasing // Constant speed for a "rotating" look
            ),
            repeatMode = RepeatMode.Restart // Single direction sweep
        ),
        label = "gradient_sweep_progress"
    )

    // The gradient definition: transparent ends, solid colors in the middle
    val taperedColors = remember(baseColors) {
        listOf(
            Color.Transparent, // 0%
            baseColors[0],     // 30%
            baseColors[1],     // 50%
            baseColors[2],     // 70%
            Color.Transparent  // 100%
        )
    }

    drawBehind {
        val strokeWidthPx = thickness.toPx()
        val yPosition = strokeWidthPx / 2f

        // The brush needs to be very wide relative to the container to simulate the smooth sweep
        val brushWidth = size.width * 3f

        // Calculate the starting X offset. We move the entire wide brush from
        // completely off the left screen to completely off the right screen.
        val startX = -brushWidth + (size.width + brushWidth) * animationProgress

        val brush = Brush.linearGradient(
            colors = taperedColors,
            start = Offset(startX, yPosition),
            end = Offset(startX + brushWidth, yPosition),
            tileMode = TileMode.Clamp
        )

        // Draw the line segment that is visible within the bounds of your TextField
        drawLine(
            brush = brush,
            start = Offset(0f, yPosition),
            end = Offset(size.width, yPosition),
            strokeWidth = strokeWidthPx
        )
    }
}
