package com.example.yummiziapp.features.intro.ui.intro

sealed interface IntroUiEvent {
    data object NavigateToHome: IntroUiEvent
}