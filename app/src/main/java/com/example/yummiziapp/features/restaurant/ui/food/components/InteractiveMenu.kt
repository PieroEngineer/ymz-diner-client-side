package com.example.yummiziapp.features.restaurant.ui.food.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.features.restaurant.ui.model.MenuUiModel
import kotlin.uuid.Uuid
import com.example.yummiziapp.R
import com.example.yummiziapp.core.designsystem.theme.YummiziImage

@Composable
fun InteractiveMenu(
    modifier: Modifier = Modifier,
    menuUiModel: MenuUiModel,
    onUpdatingSelectionQuantityClicked: (dishId: Uuid, delta: Int) -> Unit
) {
    val spacing = YummiziTheme.spacing
    // The Outer Box matches the height of the screen/container
    Box(
        modifier = modifier
//            .fillMaxSize()
            .wrapContentWidth(), // Critical: Makes the box only as wide as the scaled image
        contentAlignment = Alignment.Center

    ) {
        Log.d("MyActivity", "📇  Menu is going to be showed. Menu info:\n${menuUiModel}\n")

        // The Background Menu Image
        YummiziImage(
            model = menuUiModel.page.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxHeight()
        )


        // The Coordinate Overlay System
        // BoxWithConstraints lets us get the exact width/height the image is currently taking
        BoxWithConstraints(
            modifier = Modifier.matchParentSize()
        ) {
            val containerWidth = maxWidth
            val containerHeight = maxHeight

            for ((i, element) in menuUiModel.dishIds.withIndex()) {
                // Calculate position
                // We subtract half the icon size (e.g., 20.dp) to center the icon on the point
                val xOffset = containerWidth * menuUiModel.coordinatesInPages[i].x
                val yOffset = containerHeight * menuUiModel.coordinatesInPages[i].y

                Box(
                    modifier = Modifier
                        .offset(
                            x = xOffset - spacing.regularIconSize * .5f,
                            y = yOffset - spacing.regularIconSize * .5f
                        )
                        .size(spacing.regularIconSize) // Size of your small button
                        .clip(CircleShape)
                        .clickable { onUpdatingSelectionQuantityClicked(element, 1) },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dish_selection_button),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}