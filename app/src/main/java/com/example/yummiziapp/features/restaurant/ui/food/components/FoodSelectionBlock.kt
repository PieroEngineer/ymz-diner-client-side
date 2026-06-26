package com.example.yummiziapp.features.restaurant.ui.food.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.R
import com.example.yummiziapp.features.restaurant.ui.model.FoodSelectionUiModel
import kotlin.uuid.Uuid

@Composable
fun FoodSelectionBlock(
    modifier: Modifier = Modifier,

    foodSelection: FoodSelectionUiModel,

    onUpdatingSelectionQuantityClicked: (dishId: Uuid, delta: Int) -> Unit,

    ){
    val spacing = YummiziTheme.spacing


    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ){

        Text(
            text = foodSelection.name,
            textAlign = TextAlign.Start,
            color = YummiziTheme.colors.yummziziDarkBlue,
            style = YummiziTheme.typography.teacherFontVar3,
            modifier = Modifier
                .width(120.dp)  //TODO: In UI model (or other place to not hardcore)
                .padding(vertical = spacing.smallPadding)
                .padding(start = spacing.mediumPadding)
        )

        Spacer(Modifier.weight(5.9f))

        Row(    //TODO: Convert it into a function, because it's used in other parts too
            modifier = Modifier
                .border(
                    BorderStroke(
                        spacing.mediumLineThickness,
                        YummiziTheme.colors.yummziziLightOrange
                    ),
                    shape = RoundedCornerShape(spacing.regularCornerRadius)
                )
                .clip(
                    RoundedCornerShape(50)
                )
                .weight(40.85f),
            verticalAlignment = Alignment.CenterVertically
        ){


            Image(
                painter = painterResource(R.drawable.row_btn),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = YummiziTheme.colors.yummziziLightBlue2),
                modifier = Modifier
                    .padding(start = spacing.mediumPadding)
                    .padding(vertical = spacing.smallPadding)
                    .size(spacing.smallIconSize)
                    .rotate(180f)
                    .clickable { onUpdatingSelectionQuantityClicked(foodSelection.id, -1) }

            )

            Spacer(Modifier.weight(1f))

            Text(
                text = foodSelection.amount.toString(),
                textAlign = TextAlign.Center,
                color = YummiziTheme.colors.yummziziDarkOrange,
                style = YummiziTheme.typography.comfortaaFontVar1,
                modifier = Modifier
                    .padding(vertical = spacing.smallPadding)
            )

            Spacer(Modifier.weight(1f))

            Image(
                painter = painterResource(R.drawable.row_btn),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = YummiziTheme.colors.yummziziLightBlue2),
                modifier = Modifier
                    .padding(end = spacing.mediumPadding)
                    .padding(vertical = spacing.smallPadding)
                    .size(spacing.smallIconSize)
                    .clickable { onUpdatingSelectionQuantityClicked(foodSelection.id, 1) }

            )
        }

        Spacer(Modifier.weight(7.78f))

        Text(   //TODO: Replace later the currency for the variable
            text = "S/. ${foodSelection.accumulatedCost}",
            color = YummiziTheme.colors.yummziziLightOrange,
            style = YummiziTheme.typography.teacherFontVar1,
            modifier = Modifier
                .weight(34f)
        )

        Spacer(Modifier.weight(1f))

        Image(
            painter = painterResource(R.drawable.delete_button),
            contentDescription = null,
            modifier = Modifier
                .padding(end = spacing.mediumPadding)
                .padding(vertical = spacing.smallPadding)
                .size(spacing.mediumIconSize)
        )

    }


}