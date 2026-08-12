package com.example.yummiziapp.features.auth.ui.emailmethod

import com.example.yummiziapp.features.auth.ui.model.SignInUiModel
import com.example.yummiziapp.features.auth.ui.model.SignUpUiModel

sealed interface EmailMethodUiState {
    data object Loading : EmailMethodUiState
    data class SignIn(val userInfo: SignInUiModel) : EmailMethodUiState
    data class SignUp(val userInfo: SignUpUiModel) : EmailMethodUiState
    data class Error(val exception: Throwable) : EmailMethodUiState
}