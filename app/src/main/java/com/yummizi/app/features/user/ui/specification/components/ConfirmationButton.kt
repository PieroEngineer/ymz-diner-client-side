package com.yummizi.app.features.user.ui.specification.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.yummizi.app.core.designsystem.theme.YummiziTheme

@Composable
fun ConfirmationButton(
    modifier: Modifier = Modifier,

    onClick: () -> Unit,
    text: String
){
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(YummiziTheme.spacing.smallCornerRadius))
            .background(YummiziTheme.colors.yummiziBlue)
            .clickable { onClick() }
            .padding(horizontal = YummiziTheme.spacing.regularPadding, vertical = YummiziTheme.spacing.mediumPadding),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = text,
            color = YummiziTheme.colors.yummiziWhite,
            style = YummiziTheme.typography.teacherFontVar1,
            textAlign = TextAlign.Center

        )
    }
}
