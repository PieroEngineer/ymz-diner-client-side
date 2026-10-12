package com.yummizi.app.features.home.ui.feed.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.yummizi.app.R
import com.yummizi.app.core.designsystem.theme.YummiziTheme

@Composable
fun MiniTutorialModal(
    modifier: Modifier = Modifier,
    isOpen: Boolean = false,
    isNotShowAgainChecked: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onNotShowAgainChange: (Boolean) -> Unit = {},
    onCloseClicked: () -> Unit = {}
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
                    .background(YummiziTheme.colors.yummiziBlack.copy(alpha = 0.5f))
                    .pointerInput(Unit) {
                        // Swallow touch configurations
                    }
                    .clickable { onDismissRequest() }
            )

            // THE POP-UP FRAME
            Column(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* Consume taps to prevent click-through */ }
                    .clip(
                        RoundedCornerShape(
                            topStart = spacing.mediumCornerRadius,
                            topEnd = spacing.bigCornerRadius,
                            bottomStart = spacing.bigCornerRadius,
                            bottomEnd = spacing.mediumCornerRadius
                        )
                    )
                    .border(
                        BorderStroke(spacing.mediumLineThickness, YummiziTheme.colors.yummiziDarkOrange),
                        shape = RoundedCornerShape(
                            topStart = spacing.mediumCornerRadius,
                            topEnd = spacing.bigCornerRadius,
                            bottomStart = spacing.bigCornerRadius,
                            bottomEnd = spacing.mediumCornerRadius
                        )
                    )
                    .background(YummiziTheme.colors.yummiziWhite)
                    .fillMaxWidth(0.85f)
                    .padding(spacing.bigPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                HorizontalDivider(
                    thickness = spacing.mediumLineThickness,
                    color = YummiziTheme.colors.yummiziDarkOrange,
                    modifier = Modifier.fillMaxWidth(0.5f)
                )

                Spacer(Modifier.height(spacing.regularSeparation))

                Image(
                    painter = painterResource(id = R.drawable.colored_minitutorial_icon),
                    contentDescription = null,
                    modifier = Modifier.size(spacing.bigImageSize)
                )

                Spacer(Modifier.height(spacing.regularSeparation))

                Text(
                    text = "Mantén presionado en un restaurante y arrástralo hasta el bloque superior para solicitar mesa",
                    color = YummiziTheme.colors.yummiziDarkBlue,
                    style = YummiziTheme.typography.teacherFontVar1,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(spacing.mediumSeparation))

                HorizontalDivider(
                    thickness = spacing.mediumLineThickness,
                    color = YummiziTheme.colors.yummiziDarkOrange,
                    modifier = Modifier.fillMaxWidth(0.5f)
                )

                Spacer(Modifier.height(spacing.regularSeparation))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onNotShowAgainChange(!isNotShowAgainChecked) }
                ) {
                    Checkbox(
                        checked = isNotShowAgainChecked,
                        onCheckedChange = onNotShowAgainChange,
                        colors = CheckboxDefaults.colors(
                            checkedColor = YummiziTheme.colors.yummiziDarkOrange,
                            uncheckedColor = YummiziTheme.colors.yummiziDarkOrange,
                            checkmarkColor = YummiziTheme.colors.yummiziWhite
                        )
                    )
                    Text(
                        text = "No volver a mostrar",
                        color = YummiziTheme.colors.yummiziDarkBlue,
                        style = YummiziTheme.typography.teacherFontVar3
                    )
                }

                Spacer(Modifier.height(spacing.mediumSeparation))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(spacing.smallCornerRadius))
                        .background(YummiziTheme.colors.yummiziBlue)
                        .clickable { onCloseClicked() }
                ) {
                    Text(
                        text = "Entendido",
                        textAlign = TextAlign.Center,
                        color = YummiziTheme.colors.yummiziWhite,
                        style = YummiziTheme.typography.teacherFontVar1,
                        modifier = Modifier
                            .padding(horizontal = spacing.mediumPadding)
                            .padding(vertical = spacing.smallPadding)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}
