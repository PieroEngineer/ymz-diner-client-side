package com.yummizi.app.features.auth.ui.emailmethod

import com.yummizi.app.features.auth.ui.model.SignInUiModel
import com.yummizi.app.features.auth.ui.model.SignUpUiModel

sealed interface EmailMethodUiState {
    data object Loading : EmailMethodUiState
    data class InForm( val formState: FormUiModel, val isSigningIn: Boolean): EmailMethodUiState
    data class Error(val exception: Throwable) : EmailMethodUiState
}