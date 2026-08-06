package com.example.yummiziapp.features.user.ui.specification.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
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
    onConfirmationTriggered: () -> Unit = {},
    maxWaitingTimeInMinutes: Int
) {
    val spacing = YummiziTheme.spacing

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

            // THE INTERCEPTION MASK (Locks screen inputs and darkens background)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(YummiziTheme.colors.yummiziBlack.copy(alpha = 0.5f)) // Transparent dark cover
                    .pointerInput(Unit) {
                        // 🚀 Swallow all touch configurations so background elements are completely frozen!
                    }
                    .clickable { onDismissRequest() }
            )

            // THE POP-UP FRAME
            Card(
                shape = RoundedCornerShape(spacing.mediumCornerRadius),
                modifier = modifier
                    .fillMaxWidth(0.85f)
                    .padding(spacing.bigPadding)
            ) {
                Column(
                    modifier = Modifier
                        .background(YummiziTheme.colors.yummiziWhite)
                        .padding(spacing.bigPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Confirmo mi llegada en ${maxWaitingTimeInMinutes} minutos",
                        color = YummiziTheme.colors.yummiziDarkBlue,
                        style = YummiziTheme.typography.teacherFontVar2,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(spacing.bigSeparation))

                    // 🚀 3. SLIDE-TO-ACTION COMPONENT
                    SlideToConfirm(
                        onConfirm = onConfirmationTriggered
                    )

                    Spacer(modifier = Modifier.height(spacing.bigSeparation))

                    Text(
                        text = "Una vez confirme este restaurante,\nlas otras solicitudes serán canceladas",
                        color = YummiziTheme.colors.yummiziLightOrange,
                        style = YummiziTheme.typography.teacherFontVar6,
                        textAlign = TextAlign.Center
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

    val spacing = YummiziTheme.spacing

    val thumbSize = 56.dp                  // TODO: Change the hardcoded values to theme values
    val trackHeight = 64.dp               //
    val thumbPadding = 4.dp           //
    
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
            .background(YummiziTheme.colors.yummiziDarkOrange.copy(alpha = 0.15f))
            .border(
                width = 2.dp,
                color = YummiziTheme.colors.yummiziDarkOrange,
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
            color = YummiziTheme.colors.yummiziDarkOrange,
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
                .background(YummiziTheme.colors.yummiziDarkOrange),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.row_btn),
                contentDescription = null,

                tint = Color.White,
                // Rotate 90 degrees
                modifier = Modifier
                    .size(28.dp)
                    .rotate(90f)

            )
        }
    }
}
