package com.example.yummiziapp.features.home.ui.components

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.DpOffset
import com.example.yummiziapp.core.data.local.entities.Restaurant
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

//@Preview
@Composable
fun RestaurantBlock(
    modifier: Modifier = Modifier,

    restaurant: Restaurant,
    reelActivated: Boolean = false,

    onDragStart: (Restaurant, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    val spacing = YummiziTheme.spacing
    var blockPosition by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .dropShadow( // Apply the intense drop shadow
                shape = CircleShape,
                //shape = shape,
                shadow = Shadow(
                    color = YummiziTheme.colors.yummziziBlack.copy(alpha = .5f),
                    radius = 10.dp,                          // High radius for a wide, soft spread
                    offset = DpOffset(x = 0.dp, y = 15.dp), // Offset only to the bottom
                )
            )
            .clip(RoundedCornerShape(spacing.mediumCornerRadius))
            .onGloballyPositioned {
                blockPosition = it.positionInWindow()
            }
    ){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(restaurant.id) { // Move pointerInput here
                    // This function detects a long press followed by a drag gesture.
                    detectDragGesturesAfterLongPress(
                        // Called when a long press is detected and the drag starts.
                        onDragStart = { offset ->
                            // The initial drag position is the block's position plus the touch offset within the block.
                            onDragStart(restaurant, blockPosition + offset)
                        },
                        // Called as the finger moves across the screen.
                        onDrag = { change, dragAmount ->
                            // Consume the position change to prevent other gestures from being triggered
                            change.consume()
                            // Pass the amount of movement (delta) to the parent to update the drag position
                            onDrag(dragAmount)
                        },
                        // Called when the user lifts their finger.
                        onDragEnd = {
                            onDragEnd()
                        },
                        // Called if the gesture is canceled (e.g., by another gesture taking precedence).
                        onDragCancel = {
                            onDragEnd()
                        }
                    )
                }
        ){
            ImageReel(restaurant.dishesToShowLink, reelActivated)

            Log.d("MyActivity", "🖼️  Link of the logo images ${restaurant.logoUrl}\n")
            AsyncImage(
                model = restaurant.logoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(spacing.reducedImageSize)
                    .clip(RoundedCornerShape(spacing.mediumCornerRadius)),
                alignment = Alignment.TopStart
            )
        }
    }
}