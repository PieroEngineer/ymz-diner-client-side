package com.example.yummiziapp.core.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class YummiziSpacing (

    val tinyPadding: Dp = 1.dp,
    val smallPadding: Dp = 4.dp,
    val mediumPadding: Dp = 10.dp,
    val regularPadding: Dp = 18.dp,
    val bigPadding: Dp = 28.dp,

    val tinySeparation: Dp = 2.dp,
    val smallSeparation: Dp = 5.dp,
    val mediumSeparation: Dp = 10.dp,
    val regularSeparation: Dp = 15.dp,
    val bigSeparation: Dp = 20.dp,
    val hugeSeparation: Dp = 30.dp,

    val tinyCornerRadius: Dp = 5.dp,
    val smallCornerRadius: Dp = 10.dp,
    val mediumCornerRadius: Dp = 20.dp,
    val regularCornerRadius: Dp = 40.dp,
    val bigCornerRadius: Dp = 60.dp,

    val smallLineThickness: Dp = 1.dp,
    val mediumLineThickness: Dp = 2.dp,
    val bigLineThickness: Dp = 3.dp,

    val tinyIconSize: Dp = 10.dp,
    val smallIconSize: Dp = 18.dp,
    val mediumIconSize: Dp = 25.dp,
    val regularIconSize: Dp = 30.dp,
    val bigIconSize: Dp = 40.dp,
    val hugeIconSize: Dp = 60.dp,
    val enormousIconSize: Dp = 80.dp,

    val reducedImageSize: Dp = 60.dp,
    val smallImageSize: Dp = 80.dp,
    val mediumImageSize: Dp = 120.dp,
    val bigImageSize: Dp = 200.dp,

    // Create for radius and offsets

    val barHeight: Dp = 60.dp,
    val frameHeight: Dp = 200.dp

)

val LocalYummiziSpacing = staticCompositionLocalOf { YummiziSpacing() }