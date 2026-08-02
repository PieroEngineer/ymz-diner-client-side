package com.example.yummiziapp.features.payment.ui.arrival.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

/**
 * High-performance countdown timer text component.
 * Uses localized state production to isolate 1-second ticks from parent recomposition scopes.
 */
@Composable
fun RemainingTimeText(
    modifier: Modifier = Modifier,

    targetInstant: Instant,
    expiredText: String = "Tiempo finalizado",

    style: TextStyle = MaterialTheme.typography.bodyMedium, //X
    color: Color = Color.Unspecified                                         //
) {
    // Isolated state producer: Updates once per second without triggering parent recomposition
    val remainingFormattedText by produceState(
        initialValue = formatRemainingTime(Clock.System.now(), targetInstant, expiredText),
        key1 = targetInstant,
        key2 = expiredText
    ) {
        while (true) {
            val now = Clock.System.now()
            value = formatRemainingTime(now, targetInstant, expiredText)

            if (now > targetInstant || now == targetInstant) {
                break // Stop coroutine execution once expired to save CPU
            }
            delay(1000L.milliseconds)
        }
    }

    Text(
        text = remainingFormattedText,
        modifier = modifier,
        style = style,
        color = color
    )
}

private fun formatRemainingTime(
    now: Instant,
    target: Instant,
    expiredText: String
): String {
    if (now > target || now == target) {
        return expiredText
    }
    val duration = target - now
    // Destructure minutes and seconds at the same time
    val (minutes, seconds) = duration.toComponents { minutes, seconds, _, _ ->
        Pair(minutes, seconds)
    }

    return "$minutes min $seconds seg"
}