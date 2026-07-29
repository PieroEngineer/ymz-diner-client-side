package com.example.yummiziapp.features.restaurant.ui.food.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.features.restaurant.ui.model.FoodSelectionUiModel
import com.example.yummiziapp.features.restaurant.ui.model.Identifiable
import com.example.yummiziapp.features.restaurant.ui.model.MenuSetting
import kotlin.uuid.Uuid

@Composable
fun <T: Identifiable> MenuSettingArea(
    modifier: Modifier = Modifier,
    menuStructure: MenuSetting<T>,
    onUpdatingSelectionQuantityClicked: (dishId: Uuid, delta: Int) -> Unit,
    onRemoveDishClicked: (dishId: Uuid) -> Unit,
    totalOrderCost: String
){
    val spacing = YummiziTheme.spacing
    val listState = rememberLazyListState()

    Column(  // TODO: Later, this column is contained in a horizontal pager *2
        modifier = modifier
    ){
        Box(    // Header bar
            modifier = Modifier
                .padding(horizontal = spacing.regularPadding, vertical = spacing.smallPadding)
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(20)
                )
                .background(YummiziTheme.colors.yummiziWhite)
                .align(Alignment.CenterHorizontally)
        ){

            Image(
                painter = painterResource(id = menuStructure.topButtonImageId),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = spacing.smallPadding)
                    .padding(vertical = spacing.mediumPadding)
                    .size(spacing.regularIconSize)
                    .align(Alignment.CenterStart)
            )

            Text(
                text = menuStructure.title,
                textAlign = TextAlign.Center,
                color = YummiziTheme.colors.yummiziLightOrange,
                style = YummiziTheme.typography.teacherFontVar5,
                modifier = Modifier
                    .padding(start = spacing.smallPadding)
                    .padding(vertical = spacing.mediumPadding)
                    .align(Alignment.Center)
            )
        }

        Spacer(Modifier.height(spacing.smallSeparation))

        Column(    // Manipulable area
            modifier = Modifier
                .padding(horizontal = spacing.regularPadding, vertical = spacing.smallPadding)
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(5)
                )
                .background(YummiziTheme.colors.yummiziWhite.copy(alpha = 0.8f)),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            LazyColumn(    // Current selected restaurants
                state = listState,
                verticalArrangement = Arrangement.spacedBy(spacing.regularSeparation),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = spacing.mediumPadding)
                    .padding(top = spacing.mediumPadding)
                    .weight(8f)
                //.weight(1f)
            ) {
                items(
                    items = menuStructure.elementsInScroll,
                    key = { selection -> selection.id }
                ) { selection ->

                    when (menuStructure) {
                        is MenuSetting.CurrentSelections -> {
                            FoodSelectionBlock(
                                foodSelection = selection as FoodSelectionUiModel,
//X                                onUpdatingSelectionQuantityClicked = onUpdatingSelectionQuantityClicked
                                onUpdatingSelectionQuantityClicked = {
                                    increaseQuantity ->
                                    onUpdatingSelectionQuantityClicked(
                                        selection.id,
                                        if (increaseQuantity) 1 else -1
                                    )
                                },
                                onRemoveDishClicked = { onRemoveDishClicked(selection.id) }
                            )
                        }
                    }
                }
            }

            when (menuStructure) {
                is MenuSetting.CurrentSelections -> {
                    Spacer(Modifier.weight(1f))

                    Column(
                        modifier = Modifier
                            .padding(horizontal = spacing.mediumPadding)
                            .padding(bottom = spacing.mediumPadding)
                            .fillMaxWidth(.8f)
                            .weight(2f)
                    ){
                        HorizontalDivider(
                            modifier = Modifier
                                .weight(1f),
                            thickness = spacing.mediumLineThickness,
                            color = YummiziTheme.colors.yummiziLightOrange
                        )

                        Box(
                            modifier = Modifier
                                .weight(2f)
                                .wrapContentHeight()
                        ){

                            Text(
                                text = "S/. $totalOrderCost",
                                textAlign = TextAlign.End,
                                color = YummiziTheme.colors.yummiziLightOrange,
                                style = YummiziTheme.typography.teacherFontVar4,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }

                }
            }

        }
    }
}