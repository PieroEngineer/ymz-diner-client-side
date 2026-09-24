package com.yummizi.app.features.home.ui.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import com.yummizi.app.core.data.local.entities.Restaurant
import com.yummizi.app.core.designsystem.theme.YummiziTheme
import com.yummizi.app.core.designsystem.theme.AsyncYummiziImage

@Composable
// This composable represents the visual feedback for the restaurant being dragged
fun DragOverlay(
    restaurant: Restaurant,     // TODO: (#919)
    position: Offset,

    itemWidth: Dp,
    itemHeight: Dp
) {
    val spacing = YummiziTheme.spacing
    Box(
        modifier = Modifier
            .graphicsLayer {
                translationX = position.x
                translationY = position.y
            }
            .height(itemHeight)
            .width(itemWidth)
            .background(
                YummiziTheme.colors.yummiziWhite,
                RoundedCornerShape(spacing.mediumCornerRadius * .75f)
            ),
        contentAlignment = Alignment.Center
    ) {
        val spacing = YummiziTheme.spacing
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.regularPadding),
            verticalAlignment = Alignment.CenterVertically
        ){
            AsyncYummiziImage(
                model = restaurant.logoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(spacing.reducedImageSize)
                    .clip(RoundedCornerShape(spacing.mediumCornerRadius))
            )

            Spacer(Modifier.height(spacing.mediumLineThickness))

            Text(
                text = restaurant.name,
                style = YummiziTheme.typography.comfortaaFontVar5,
                color = YummiziTheme.colors.yummiziBlack
            )
        }
    }
}