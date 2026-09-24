package com.yummizi.app.features.auth.ui.emailmethod

sealed interface EmailMethodUiEvent {
    data object NavigateToIntro: EmailMethodUiEvent
    data object NavigateToHome: EmailMethodUiEvent
}