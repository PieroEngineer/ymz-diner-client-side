package com.example.yummiziapp.features.contact.ui.contact

sealed interface ContactUiEvent {
    data object NavigateToHome : ContactUiEvent
    data object NavigateToPreferences : ContactUiEvent
}