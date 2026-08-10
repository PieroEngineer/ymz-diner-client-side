package com.example.yummiziapp.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme

@Composable
fun SquareButton(    // TODO: Move to the core
    modifier: Modifier = Modifier,

    ownImageId: Int,
    ownText: String,
    iconSize: Dp = YummiziTheme.spacing.hugeIconSize,

    textColor: Color = YummiziTheme.colors.yummiziDarkBlue2,
    iconColor: Color = YummiziTheme.colors.yummiziDarkBlue,
    textStyle: TextStyle = YummiziTheme.typography.teacherFontVar1,

    onClick: () -> Unit = {},
){
    val spacing = YummiziTheme.spacing

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(spacing.mediumCornerRadius))
            .border(
                width = spacing.smallLineThickness,
                color = YummiziTheme.colors.yummiziGray,
                shape = RoundedCornerShape(spacing.mediumCornerRadius)
            )
            .background( YummiziTheme.colors.yummiziWhite)
            .padding(spacing.regularPadding)
            .clickable{
                onClick()
            }
    ){
        Image(
            painter = painterResource(ownImageId),
            colorFilter = ColorFilter.tint(color = iconColor),
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
        )

        Spacer(Modifier.height(spacing.hugeSeparation))

        Text(
            text = ownText,
            color = textColor,
            style = textStyle,
        )
    }

}