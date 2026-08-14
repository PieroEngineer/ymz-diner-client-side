package com.example.yummiziapp.features.restaurant.ui.food.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.R
import com.example.yummiziapp.features.restaurant.ui.model.FoodSelectionUiModel
import com.example.yummiziapp.features.user.ui.specification.components.NumberSelector

@Composable
fun FoodSelectionBlock(
    modifier: Modifier = Modifier,

    foodSelection: FoodSelectionUiModel,

    onUpdatingSelectionQuantityClicked: (Boolean) -> Unit,

    onRemoveDishClicked: () -> Unit,

    ){
    val spacing = YummiziTheme.spacing


    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ){

        Text(
            text = foodSelection.name,
            textAlign = TextAlign.Start,
            color = YummiziTheme.colors.yummiziDarkBlue,
            style = YummiziTheme.typography.teacherFontVar3,
            modifier = Modifier
                .width(120.dp)  //TODO: In UI model (or other place to not hardcore)
                .padding(vertical = spacing.smallPadding)
                .padding(start = spacing.mediumPadding)
        )

        Spacer(Modifier.weight(5.9f))

        NumberSelector(
            rowColor = YummiziTheme.colors.yummiziBlue2,

            indicatorNumber = foodSelection.amount,
            onNumberChanged = onUpdatingSelectionQuantityClicked
        )

        Spacer(Modifier.weight(7.78f))

        Text(   //TODO: Replace later the currency for the variable
            text = "S/. ${foodSelection.accumulatedCost}",
            color = YummiziTheme.colors.yummiziLightOrange,
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
                .clickable { onRemoveDishClicked() }
        )

    }


}