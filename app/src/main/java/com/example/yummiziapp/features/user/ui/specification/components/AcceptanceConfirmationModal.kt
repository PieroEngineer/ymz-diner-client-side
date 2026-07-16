package com.example.yummiziapp.features.user.ui.specification.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.yummiziapp.R
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

//@Preview
@Composable
fun AcceptanceConfirmationModal(
    modifier: Modifier = Modifier,
    isOpen: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onConfirmationTriggered: () -> Unit = {}
) {
    // 🛡️ Hardware Back Button Interception
    if (isOpen) {
        BackHandler { onDismissRequest() }
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 🔒 1. THE INTERCEPTION MASK (Locks screen inputs and darkens background)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)) // Transparent dark cover
                    .pointerInput(Unit) {
                        // 🚀 Swallow all touch configurations so background elements are completely frozen!
                    }
            )

            // 📦 2. THE POP-UP FRAME
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = modifier
                    .fillMaxWidth(0.85f)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Confirmar acción",
                        color = YummiziTheme.colors.yummziziDarkBlue,
                        style = YummiziTheme.typography.teacherFontVar2
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 🚀 3. SLIDE-TO-ACTION COMPONENT
                    SlideToConfirm(
                        onConfirm = onConfirmationTriggered
                    )
                }
            }
        }
    }
}

/**
 * High-performance Slide-to-Confirm component.
 * Uses Animatable and pointerInput for smooth, custom-controlled dragging.
 */
@Composable
private fun SlideToConfirm(
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val thumbSize = 56.dp
    val trackHeight = 64.dp
    val thumbPadding = 4.dp
    
    val thumbSizePx = with(density) { thumbSize.toPx() }
    val trackPaddingPx = with(density) { thumbPadding.toPx() }
    
    // Smooth animation state
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight)
            .clip(RoundedCornerShape(trackHeight / 2))
            .background(YummiziTheme.colors.yummziziDarkOrange.copy(alpha = 0.15f))
            .border(
                width = 2.dp,
                color = YummiziTheme.colors.yummziziDarkOrange,
                shape = RoundedCornerShape(trackHeight / 2)
            )
            .padding(thumbPadding)
    ) {
        val widthPx = with(density) { maxWidth.toPx() }
        val maxOffset = widthPx - thumbSizePx - (trackPaddingPx * 2)
        
        // Hint Text (fades out as user slides)
        Text(
            text = "Desliza para confirmar",
            modifier = Modifier
                .align(Alignment.Center)
                .alpha((1f - (offsetX.value / maxOffset)).coerceIn(0f, 1f)),
            color = YummiziTheme.colors.yummziziDarkOrange,
            style = YummiziTheme.typography.teacherFontVar3
        )

        // The Draggable Thumb
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .size(thumbSize)
                .pointerInput(maxOffset) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                // If slide reaches 80% of track, confirm and snap to end
                                if (offsetX.value > maxOffset * 0.8f) {
                                    offsetX.animateTo(maxOffset)
                                    onConfirm()
                                } else {
                                    // Otherwise, bounce back to start
                                    offsetX.animateTo(0f)
                                }
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                // Snap the thumb to the drag position within bounds
                                offsetX.snapTo((offsetX.value + dragAmount).coerceIn(0f, maxOffset))
                            }
                        }
                    )
                }
                .clip(RoundedCornerShape(thumbSize / 2))
                .background(YummiziTheme.colors.yummziziDarkOrange),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.row_btn),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
