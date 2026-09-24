package com.yummizi.app.features.contact.ui.contact


sealed interface ContactUiEvent {
    data object NavigateToHome : ContactUiEvent
    data object NavigateToPreferences : ContactUiEvent
    data class IntentNavigateToLink(val url: String): ContactUiEvent
    data class CopyToClipboard(val copiedText: String, val accompanyingText: String): ContactUiEvent
}