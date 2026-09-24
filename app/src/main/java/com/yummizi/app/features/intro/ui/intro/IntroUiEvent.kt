package com.yummizi.app.features.intro.ui.intro

sealed interface IntroUiEvent {
    data object NavigateToHome: IntroUiEvent
    data class IntentNavigateToLink(val url: String): IntroUiEvent
}