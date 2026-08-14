package com.example.yummiziapp.features.auth.ui.emailmethod

sealed interface EmailMethodUiEvent {
    data object NavigateToIntro: EmailMethodUiEvent
    data object NavigateToHome: EmailMethodUiEvent
}