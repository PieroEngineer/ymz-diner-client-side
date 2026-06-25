package com.example.yummiziapp.features.user.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.features.restaurant.ui.model.SelectableUiModel
import kotlin.uuid.Uuid


@Composable
fun SelectionSection(
    modifier: Modifier = Modifier,

    listedRestaurants: List<SelectableUiModel>,

    onRemoveRestaurant : (Uuid) -> Unit = {},

    onRestaurantSelected: (Uuid) -> Unit,
){
    val spacing = YummiziTheme.spacing

    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    bottomStart = spacing.mediumCornerRadius,
                    bottomEnd = spacing.regularSeparation
                )
            )
            .background(YummiziTheme.colors.yummziziLightOrange2)
            .padding(horizontal = spacing.mediumPadding)
            .padding(vertical = spacing.regularPadding)
    ) {
        SelectableRestaurants(
            listedRestaurants = listedRestaurants,
            onRemoveRestaurant = onRemoveRestaurant,

            onRestaurantSelected = onRestaurantSelected
        )


        if (listedRestaurants.isNotEmpty()) {
            // SelectedRestaurant(...)
        } else {
            // Optional: Show a message like "No hay restaurantes seleccionados"
            Text("No hay selecciones", color = Color.Gray)
        }

    }
}