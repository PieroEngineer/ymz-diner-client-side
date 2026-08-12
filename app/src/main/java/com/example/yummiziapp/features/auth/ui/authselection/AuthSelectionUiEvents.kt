package com.example.yummiziapp.features.auth.ui.authselection

sealed interface AuthSelectionUiEvents {
    data object NavigateToEmailLogin : AuthSelectionUiEvents
    data object NavigateToHome : AuthSelectionUiEvents
    data class ShowToast(val message: String) : AuthSelectionUiEvents
}