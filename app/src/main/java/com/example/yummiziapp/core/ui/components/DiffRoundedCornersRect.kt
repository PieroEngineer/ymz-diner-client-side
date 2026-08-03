package com.example.yummiziapp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextFieldDefaults.shape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme

@Composable
fun DiffRoundedCornersRect( //TODO: Delete this composable. The rounded corners are made with "RoundedCornerShape"
    modifier: Modifier = Modifier,

    shadowOn: Boolean = true,

    topStart: Dp,
    topEnd: Dp,
    bottomStart: Dp,
    bottomEnd: Dp
) {
    val spacing = YummiziTheme.spacing
    Box(
        modifier = modifier
            .dropShadow( // Apply the intense drop shadow
                shape = shape,
                shadow = Shadow(
                    color = YummiziTheme.colors.yummiziBlack.copy(alpha = if (shadowOn) 0.5f else 0f),
                    radius = 30.dp,                        // High radius for a wide, soft spread
                    offset = DpOffset(x = 15.dp, y = 20.dp), // Offset only to the bottom
                )
            )
            .clip(
                RoundedCornerShape(
                    topStart = topStart,
                    bottomEnd = bottomEnd,
                    topEnd = topEnd,
                    bottomStart = bottomStart
                )
            )
            .background(YummiziTheme.colors.yummiziLightOrange2)
            .fillMaxSize()
    )
}