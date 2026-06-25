package com.example.yummiziapp.features.restaurant.ui.menu

import com.example.yummiziapp.features.restaurant.ui.model.MenuUiModel

sealed interface MenuUiState {
    data object Loading : MenuUiState
    data class Success(val pages: List<MenuUiModel>) : MenuUiState
    data class Error(val message: String) : MenuUiState
}