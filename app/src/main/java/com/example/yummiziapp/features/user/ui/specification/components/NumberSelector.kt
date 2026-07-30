package com.example.yummiziapp.features.user.ui.specification.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.yummiziapp.R
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme

@Composable
fun NumberSelector(
    modifier: Modifier = Modifier,

    borderColor: Color = YummiziTheme.colors.yummiziDarkOrange,
    rowColor: Color = YummiziTheme.colors.yummiziDarkOrange,

    indicatorNumber: Int,
    onNumberChanged: (Boolean) -> Unit,
    maxValue: Int = 100,    ///  What would be the maximum? I can conditionally use a conditional, but AI says that it isn't readbly
){
    val spacing = YummiziTheme.spacing

    Row(
        modifier = modifier
            .border(
                BorderStroke(
                    spacing.mediumLineThickness,
                    borderColor
                ),
                shape = RoundedCornerShape(spacing.regularCornerRadius)
            )
            .clip(
                RoundedCornerShape(40)
            ),
        verticalAlignment = Alignment.CenterVertically
    ){

        Image(
            painter = painterResource(R.drawable.row_btn),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = rowColor),
            modifier = Modifier
                .padding(start = spacing.mediumPadding)
                .padding(vertical = spacing.smallPadding)
                .size(spacing.regularIconSize)
                .rotate(180f)
                .clickable {if (indicatorNumber > 0) onNumberChanged(false)} //TODO: Include bouncing effect
        )

        Spacer(Modifier.width(spacing.mediumSeparation))

        Text(
            text = indicatorNumber.toString(),
            textAlign = TextAlign.Center,
            color = borderColor,
            style = YummiziTheme.typography.comfortaaFontVar1,
            modifier = Modifier
                .padding(vertical = spacing.smallPadding)
        )

        Spacer(Modifier.width(spacing.mediumSeparation))

        Image(
            painter = painterResource(R.drawable.row_btn),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = rowColor),
            modifier = Modifier
                .padding(end = spacing.mediumPadding)
                .padding(vertical = spacing.smallPadding)
                .size(spacing.regularIconSize)
                .clickable {if (indicatorNumber < maxValue) onNumberChanged(true)}
        )
    }
}