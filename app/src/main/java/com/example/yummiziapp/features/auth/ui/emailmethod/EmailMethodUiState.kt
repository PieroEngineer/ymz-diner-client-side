package com.example.yummiziapp.features.auth.ui.emailmethod

import com.example.yummiziapp.features.auth.ui.model.SignInUiModel
import com.example.yummiziapp.features.auth.ui.model.SignUpUiModel

sealed interface EmailMethodUiState {
    data object Loading : EmailMethodUiState
    data class InForm( val formState: FormUiModel, val isSigningIn: Boolean): EmailMethodUiState
    data class Error(val exception: Throwable) : EmailMethodUiState
}