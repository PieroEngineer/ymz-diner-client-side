package com.yummizi.app.features.user.ui.profile

sealed interface ProfileUiEvent{
    data object NavigateToHome : ProfileUiEvent
    data object NavigateToContact : ProfileUiEvent
    data object NavigateToLogin : ProfileUiEvent
}