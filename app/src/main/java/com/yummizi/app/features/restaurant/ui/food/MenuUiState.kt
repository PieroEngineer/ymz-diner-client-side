package com.yummizi.app.features.restaurant.ui.food

import com.yummizi.app.features.restaurant.ui.model.MenuUiModel

sealed interface MenuUiState {
    data object Loading : MenuUiState
    data class Success(val pages: List<MenuUiModel>) : MenuUiState
    data class Error(val message: String) : MenuUiState
}