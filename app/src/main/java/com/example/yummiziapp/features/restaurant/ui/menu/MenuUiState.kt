package com.example.yummiziapp.features.restaurant.ui.menu

sealed interface MenuUiState {
    data object Loading : MenuUiState
    data class Success(val pages: List<MenuUiModel>) : MenuUiState
    data class Error(val message: String) : MenuUiState
}