package com.yummizi.app.features.restaurant.ui.food.components

import androidx.compose.ui.Modifier
import com.yummizi.app.features.restaurant.ui.model.ExtraCommentariesUiState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import com.yummizi.app.core.designsystem.theme.YummiziTheme
import com.yummizi.app.core.ui.components.YummiziMultipleLineTextField

@Composable
fun FinishedTimeModal(
    modifier: Modifier = Modifier,

    isOpen: Boolean,

    onAcceptFinishedTime: () -> Unit
){
    val spacing = YummiziTheme.spacing

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
            )

            // THE POP-UP FRAME
            Column(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* 🛡️ Consume taps to prevent "click-through" to the background mask */ }
                    .clip(
                        RoundedCornerShape(
                            topStart = spacing.mediumCornerRadius,
                            topEnd = spacing.bigCornerRadius,
                            bottomStart = spacing.bigCornerRadius,
                            bottomEnd = spacing.mediumCornerRadius
                        )
                    )
                    .border(
                        BorderStroke(
                            spacing.mediumLineThickness,
                            YummiziTheme.colors.yummiziDarkOrange
                        ),
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
            ){

                HorizontalDivider(
                    thickness = spacing.mediumLineThickness,
                    color = YummiziTheme.colors.yummiziDarkOrange,
                    modifier = Modifier.fillMaxWidth(.5f)
                )

                Spacer(Modifier.height(spacing.regularSeparation))

                Text(
                    text = "Han transcurrido más de 5 minutos de selección, por favor reintentar",
                    color = YummiziTheme.colors.yummiziDarkBlue,
                    style = YummiziTheme.typography.teacherFontVar1,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(spacing.mediumSeparation))

                HorizontalDivider(
                    thickness = spacing.mediumLineThickness,
                    color = YummiziTheme.colors.yummiziDarkOrange,
                    modifier = Modifier.fillMaxWidth(.5f)
                )

                Spacer(Modifier.height(spacing.mediumSeparation))

                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(spacing.smallCornerRadius)
                        )
                        .background(YummiziTheme.colors.yummiziRed)
                        .clickable {
                            onAcceptFinishedTime()
                        }
                ){
                    Text(
                        text = "Aceptar",
                        textAlign = TextAlign.Center,
                        color = YummiziTheme.colors.yummiziWhite,
                        style = YummiziTheme.typography.teacherFontVar1,
                        modifier = Modifier
                            .padding(horizontal = spacing.mediumPadding)
                            .padding(vertical = spacing.smallPadding)
                            .align(Alignment.Center)
                    )
                }


                Spacer(Modifier.height(spacing.mediumSeparation))

                HorizontalDivider(
                    thickness = spacing.mediumLineThickness,
                    color = YummiziTheme.colors.yummiziDarkOrange,
                    modifier = Modifier.fillMaxWidth(1f)
                )

                Spacer(Modifier.height(spacing.regularSeparation))

                Text(
                    text = "Disculpe, los restaurantes no pueden guardar una mesa por mucho tiempo",
                    color = YummiziTheme.colors.yummiziLightOrange,
                    style = YummiziTheme.typography.teacherFontVar6,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}