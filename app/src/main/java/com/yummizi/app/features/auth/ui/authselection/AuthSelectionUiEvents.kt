package com.yummizi.app.features.auth.ui.authselection

sealed interface AuthSelectionUiEvents {    /// Perhaps also include the intro for users that come back after a long time
    data object NavigateToEmailLogin : AuthSelectionUiEvents
    data object NavigateToHome : AuthSelectionUiEvents
//    data class ShowToast(val message: String) : AuthSelectionUiEvents
}