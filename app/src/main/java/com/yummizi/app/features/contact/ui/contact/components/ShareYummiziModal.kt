package com.yummizi.app.features.contact.ui.contact.components

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun ShareYummiziModal(
    modifier: Modifier = Modifier,

    isOpen: Boolean = false,
    onDismissRequest: () -> Unit = {},

//    restaurantContactUiModel: RestaurantInfoUiModel,
//
//    onTextFieldChanged: (restaurantInfo: RestaurantInfoTab, newText: String) -> Unit,

    onReady: () -> Unit,
    onCopyUrl: () -> Unit
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
                    modifier = Modifier.fillMaxWidth(.4f)
                )

                Spacer(Modifier.height(spacing.regularSeparation))

                Text(
                    text = "Agradecemos su confianza",
                    color = YummiziTheme.colors.yummiziDarkBlue,
                    style = YummiziTheme.typography.teacherFontVar1,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(spacing.regularSeparation))

                HorizontalDivider(
                    thickness = spacing.mediumLineThickness,
                    color = YummiziTheme.colors.yummiziDarkOrange,
                    modifier = Modifier.fillMaxWidth(.6f)
                )

                Spacer(Modifier.height(spacing.mediumSeparation))

                Text(
                    text = "Puedes compartirlo a un restaurante o usuario nuevo\nPara copiar el link, presiona el QR\n👇",
                    color = YummiziTheme.colors.yummiziDarkBlue,
                    style = YummiziTheme.typography.teacherFontVar3,
                    textAlign = TextAlign.Center,
                )

                Image(
                    painter = painterResource(id = R.drawable.landing_page_qr),
                    contentDescription = null, // Backgrounds are decorative
                    modifier = Modifier
                        .size(spacing.bigImageSize)
                        .clip(RoundedCornerShape(spacing.smallCornerRadius))
                        .clickable { onCopyUrl() },
                )

                Spacer(Modifier.height(spacing.mediumSeparation))

                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(spacing.smallCornerRadius)
                        )
                        .background(YummiziTheme.colors.yummiziBlue)
                        .clickable {
                            onReady()
                        }

                ){
                    Text(
                        text = "Listo",
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


/// If we go back to form way
//                YummiziMultipleLineTextField(
//                    value = restaurantContactUiModel.name,
//                    hintText = "Nombre del restaurante *",
//                    minLines = 1,
//                    onValueChange = { onTextFieldChanged(RestaurantInfoTab.NAME, it) }
//                )
//
//                Spacer(Modifier.height(spacing.mediumSeparation))
//
//                YummiziMultipleLineTextField(
//                    value = restaurantContactUiModel.contact,
//                    hintText = "Contacto (Correo / Número telefónico) *",
//                    minLines = 1,
//                    onValueChange = { onTextFieldChanged(RestaurantInfoTab.CONTACT, it) }
//                )
//
//                Spacer(Modifier.height(spacing.mediumSeparation))
//
//                YummiziMultipleLineTextField(
//                    value = restaurantContactUiModel.comment,
//                    hintText = "Comentarios...",
//                    minLines = 3,
//                    onValueChange = { onTextFieldChanged(RestaurantInfoTab.COMMENT, it) }
//                )