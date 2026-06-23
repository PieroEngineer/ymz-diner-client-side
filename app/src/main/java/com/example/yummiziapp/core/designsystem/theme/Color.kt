package com.example.yummiziapp.core.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class YummiziColors(
    val yummziziDarkOrange : Color,
    val yummziziLightOrange : Color,
    val yummziziLightOrange2 : Color,
    val yummziziDarkBlue : Color,
    val yummziziLightBlue : Color,
    val yummziziLightBlue2 : Color,
    val yummziziWhite : Color,
    val yummziziBlack : Color,
    val yummziziGray : Color,
    val yummziziGray2 : Color
)

val YummiziPalette = YummiziColors(
    yummziziDarkOrange = Color(0xFFD94500),
    yummziziLightOrange = Color(0xFFE45A04),
    yummziziLightOrange2 = Color(0xFFFF751F),
    yummziziDarkBlue = Color(0xFF001560),
    yummziziLightBlue = Color(0xFF004AAD),
    yummziziLightBlue2 = Color(0xFF005CD6),
    yummziziWhite = Color(0xFFFFFFFF),
    yummziziBlack = Color(0xFF000000),
    yummziziGray = Color(0XFFA6A6A6),
    yummziziGray2 = Color(0xFF404040)
)

val LocalYummiziColors = staticCompositionLocalOf { YummiziPalette }