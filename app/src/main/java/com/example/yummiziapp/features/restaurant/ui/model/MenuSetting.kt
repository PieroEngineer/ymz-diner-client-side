package com.example.yummiziapp.features.restaurant.ui.model

import com.example.yummiziapp.R

sealed interface MenuSetting<out T: Identifiable> {
    val title: String
    val topButtonImageId: Int
    val emptyMessage: String
    val elementsInScroll: List<T>

    data class CurrentSelections (
        override val elementsInScroll: List<FoodSelectionUiModel>
    ): MenuSetting<FoodSelectionUiModel> {
        override val title = "Tus selecciones"
        override val topButtonImageId = R.drawable.save_button
        override val emptyMessage = "Presiona en las comidas que desees"
    }
}