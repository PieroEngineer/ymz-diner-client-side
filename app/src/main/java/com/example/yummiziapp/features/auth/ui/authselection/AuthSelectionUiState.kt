package com.example.yummiziapp.features.auth.ui.authselection

sealed interface AuthSelectionUiState {
    data object Loading : AuthSelectionUiState
    data object UnauthenticatedSession: AuthSelectionUiState
}