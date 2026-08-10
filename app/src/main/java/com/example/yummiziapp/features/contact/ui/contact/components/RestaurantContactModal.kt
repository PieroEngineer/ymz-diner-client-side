package com.example.yummiziapp.features.contact.ui.contact.components

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.core.ui.components.ConformitySelector
import com.example.yummiziapp.core.ui.components.YummiziTextField
import com.example.yummiziapp.features.contact.ui.model.RestaurantInfoTab
import com.example.yummiziapp.features.contact.ui.model.RestaurantInfoUiModel

@Composable
fun RestaurantContactModal(
    modifier: Modifier = Modifier,

    isOpen: Boolean = false,
    onDismissRequest: () -> Unit = {},

    restaurantContactUiModel: RestaurantInfoUiModel,

    onTextFieldChanged: (restaurantInfo: RestaurantInfoTab, newText: String) -> Unit,

    onSend: () -> Unit,
){
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
            ){

                HorizontalDivider(
                    thickness = spacing.mediumLineThickness,
                    color = YummiziTheme.colors.yummiziDarkOrange,
                    modifier = Modifier.fillMaxWidth(.5f)
                )

                Spacer(Modifier.height(spacing.regularSeparation))

                Text(
                    text = "Agradecemos su confianza",
                    color = YummiziTheme.colors.yummiziDarkBlue,
                    style = YummiziTheme.typography.teacherFontVar1,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(spacing.mediumSeparation))

                YummiziTextField(
                    value = restaurantContactUiModel.name,
                    hintText = "Nombre del restaurante *",
                    minLines = 1,
                    onValueChange = { onTextFieldChanged(RestaurantInfoTab.NAME, it) }
                )

                Spacer(Modifier.height(spacing.mediumSeparation))

                YummiziTextField(
                    value = restaurantContactUiModel.contact,
                    hintText = "Contacto (Correo / Número telefónico) *",
                    minLines = 1,
                    onValueChange = { onTextFieldChanged(RestaurantInfoTab.CONTACT, it) }
                )

                Spacer(Modifier.height(spacing.mediumSeparation))

                YummiziTextField(
                    value = restaurantContactUiModel.comment,
                    hintText = "Comentarios...",
                    minLines = 3,
                    onValueChange = { onTextFieldChanged(RestaurantInfoTab.COMMENT, it) }
                )

                Spacer(Modifier.height(spacing.mediumSeparation))

                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(spacing.smallCornerRadius)
                        )
                        .background(YummiziTheme.colors.yummiziLightBlue)
                        .clickable {
                            onSend()
                        }

                ){
                    Text(
                        text = "Enviar",
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
                    text = "Los contactaremos en la prontitud",
                    color = YummiziTheme.colors.yummiziLightOrange,
                    style = YummiziTheme.typography.teacherFontVar6,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}