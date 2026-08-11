package com.example.yummiziapp.features.user.ui.profile

sealed interface ProfileUiEvent{
    data object NavigateToHome : ProfileUiEvent
    data object NavigateToContact : ProfileUiEvent
}