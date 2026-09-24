package com.yummizi.app.features.restaurant.ui.food

import com.yummizi.app.features.restaurant.ui.model.MenuUiModel

sealed interface FoodSelectionUiState {
    data object Loading : FoodSelectionUiState
    data class Success(val pages: List<MenuUiModel>) : FoodSelectionUiState
    data class Error(val message: String) : FoodSelectionUiState
}