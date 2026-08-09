package com.example.yummiziapp.features.feedback.ui.contact

sealed interface ContactUiEvent {
    data object NavigateToHome : ContactUiEvent
    data object NavigateToPreferences : ContactUiEvent
}