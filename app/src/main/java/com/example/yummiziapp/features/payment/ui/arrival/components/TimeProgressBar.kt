package com.example.yummiziapp.features.payment.ui.arrival.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

/**
 * Custom horizontal progress bar displaying elapsed time between two points in time.
 */
@Composable
fun TimeProgressBar(
    modifier: Modifier = Modifier,

    startInstant: Instant,
    endInstant: Instant,

    barHeight: Dp = 16.dp,                                  //
    borderWidth: Dp = 2.dp,                              //X
    primaryColor: Color = Color(0xFF001560)    //
) {
    // Calculates current progress fraction (0.0f to 1.0f) off the main UI thread path
    val rawProgress by produceState(
        initialValue = calculateProgress(Clock.System.now(), startInstant, endInstant),
        key1 = startInstant,
        key2 = endInstant
    ) {
        while (true) {
            val now: Instant = Clock.System.now()
            val currentProgress = calculateProgress(now, startInstant, endInstant)
            value = currentProgress

            if (currentProgress >= 1.0f) {
                break // Halt ticker once 100% capacity is reached
            }
            delay(500L.milliseconds) // 500ms precision offers smooth visual ticks with low CPU overhead
        }
    }

    // Smoothly interpolates layout width changes across frame updates
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 400),
        label = "TimeProgressBarAnimation"
    )

    // Layout container with 50% capsule corners (CircleShape)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
            .clip(CircleShape)
            .border(width = borderWidth, color = primaryColor, shape = CircleShape)
            .background(Color.Transparent)
    ) {
        // Active Progress Fill
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = animatedProgress)
                .background(color = primaryColor, shape = CircleShape)
        )
    }
}

private fun calculateProgress(now: Instant, start: Instant, end: Instant): Float {  // TODO: move it to a helper
    val totalDuration = end - start
    if (totalDuration <= Duration.ZERO) return 1.0f

    val elapsedDuration = now - start

    // Dividing two Durations returns a Double ratio (e.g., 0.5)
    return (elapsedDuration / totalDuration).toFloat().coerceIn(0.0f, 1.0f)
}