package com.yummizi.app.features.restaurant.ui.food.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.yummizi.app.core.designsystem.theme.YummiziTheme
import com.yummizi.app.features.restaurant.ui.model.ComplementSelectionUiState
import kotlin.uuid.Uuid

@Composable
fun ComplementSelectionModal(
    modifier: Modifier = Modifier,

    onCancel: () -> Unit = {},

    uiState: ComplementSelectionUiState,

    onDishSelected: (Uuid, Uuid) -> Unit
){
    val spacing = YummiziTheme.spacing
    val listState = rememberLazyListState()

    // 🛡️ Hardware Back Button Interception
    if (uiState.isModalOpen) {
        BackHandler { onCancel() }
    }

    AnimatedVisibility(
        visible = uiState.isModalOpen,
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
                    .clickable { onCancel() }
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
                    text = "${uiState.mainDishName} incluye un complemento, por favor seleccionelo 🥘",
                    color = YummiziTheme.colors.yummiziDarkBlue,
                    style = YummiziTheme.typography.teacherFontVar1,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(spacing.mediumSeparation))

                LazyColumn(    // Current selected restaurants
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(spacing.smallSeparation),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(spacing.mediumPadding)
                ) {
                    items(
                        items = uiState.availableComplementaryDishes,
                        key = { selection -> selection.dishId }
                    ) { selection ->
                        Text(
                            text = selection.dishName,
                            color = YummiziTheme.colors.yummiziDarkBlue,
                            style = YummiziTheme.typography.teacherFontVar1,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clickable {
                                    if(uiState.mainDishId != null){
                                      onDishSelected(uiState.mainDishId, selection.dishId) }
                                    }
                        )
                    }
                }

                Spacer(Modifier.height(spacing.mediumSeparation))

                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(spacing.smallCornerRadius)
                        )
                        .background(YummiziTheme.colors.yummiziBlue)
                        .clickable { onCancel() }

                ){
                    Text(
                        text = "Cancelar",
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
                    text = "El restaurante las hizo para ti 🫰",
                    color = YummiziTheme.colors.yummiziLightOrange,
                    style = YummiziTheme.typography.teacherFontVar6,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}