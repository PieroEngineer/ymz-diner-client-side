package com.yummizi.app.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun YummiziTheme(content: @Composable () -> Unit) {
    val spacing = YummiziSpacing()
    val colors = YummiziPalette // Ready for system dark mode logic mapping later
    val typography = YummiziTypographies

    CompositionLocalProvider(
        LocalYummiziSpacing provides spacing,
        LocalYummiziColors provides colors,
        LocalYummiziTypography provides typography
    ) {
        // We wrap your custom system inside MaterialTheme so core system ripples,
        // focus states, and foundational elements inherit structural behaviors cleanly.
        MaterialTheme {
            content()
        }
    }
}

object YummiziTheme {
    val spacing: YummiziSpacing
        @Composable get() = LocalYummiziSpacing.current

    val colors: YummiziColors
        @Composable get() = LocalYummiziColors.current

    val typography: YummiziTypography
        @Composable get() = LocalYummiziTypography.current
}