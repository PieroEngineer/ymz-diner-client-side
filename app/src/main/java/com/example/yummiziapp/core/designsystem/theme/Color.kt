package com.example.yummiziapp.core.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class YummiziColors(
    val yummiziDarkOrange : Color,
    val yummiziLightOrange : Color,
    val yummiziLightOrange2 : Color,
    val yummiziDarkBlue : Color,
    val yummiziDarkBlue2 : Color,
    val yummiziLightBlue : Color,
    val yummiziLightBlue2 : Color,
    val yummiziWhite : Color,
    val yummiziBlack : Color,
    val yummiziGray : Color,
    val yummiziGray2 : Color,
    val yummiziGray3 : Color,
    val yummiziRed : Color,
    val yummiziYellow : Color

)

val YummiziPalette = YummiziColors(
    yummiziDarkOrange = Color(0xFFD94500),
    yummiziLightOrange = Color(0xFFE45A04),
    yummiziLightOrange2 = Color(0xFFFF751F),
    yummiziDarkBlue = Color(0xFF001560),
    yummiziDarkBlue2 = Color(0xFF172545),
    yummiziLightBlue = Color(0xFF004AAD),
    yummiziLightBlue2 = Color(0xFF005CD6),
    yummiziWhite = Color(0xFFFFFFFF),
    yummiziBlack = Color(0xFF000000),
    yummiziGray = Color(0XFFA6A6A6),
    yummiziGray2 = Color(0xFF404040),
    yummiziGray3 = Color(0xFFEDECEC),
    yummiziRed = Color(0xFFFF3131),
    yummiziYellow = Color(0xFFFFE27D)
)

val LocalYummiziColors = staticCompositionLocalOf { YummiziPalette }