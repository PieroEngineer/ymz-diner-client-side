package com.yummizi.app.features.user.ui.profile

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class   Success(val user: ProfileUiModel) : ProfileUiState
    data class   Error(val message: String) : ProfileUiState
}