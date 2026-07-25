package com.example.yummiziapp.features.user.ui.specification.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.features.user.ui.model.FocusedRestaurantUiModel
import com.example.yummiziapp.features.user.ui.model.RequestUiStatus

@Composable
fun StatusNotice(
    modifier: Modifier = Modifier,

    selectedRestaurant: FocusedRestaurantUiModel
){
    val spacing = YummiziTheme.spacing

    Row(    // Request state
        modifier = modifier
            .padding(bottom = spacing.mediumPadding)
            .padding(horizontal = spacing.bigPadding)
            .then(
                if (selectedRestaurant.waitingState is RequestUiStatus.Accepted) {
                    // If the condition is true, apply the border modifier.
                    Modifier.border(
                        BorderStroke(
                            spacing.bigLineThickness,
                            YummiziTheme.colors.yummiziLightOrange2
                        ),
                        shape = RoundedCornerShape(spacing.regularSeparation)
                    )
                } else {
                    Modifier
                }
            )
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ){

        Text(
            text = selectedRestaurant.waitingState.phrase,
            textAlign = TextAlign.Start,
            color = YummiziTheme.colors.yummiziLightOrange2,
            style = YummiziTheme.typography.teacherFontVar1,
            modifier = Modifier
                .padding(vertical = spacing.smallSeparation)
                .padding(start = spacing.regularSeparation)
        )

        Spacer(Modifier.weight(1f))

        Image(
            painter = painterResource(selectedRestaurant.waitingState.iconRes),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = YummiziTheme.colors.yummiziLightOrange2),
            modifier = Modifier
                .size(spacing.smallImageSize)
                .padding(vertical = spacing.smallSeparation)
                .padding(end = spacing.regularSeparation)
        )

    }
}