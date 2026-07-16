package com.example.yummiziapp.features.user.ui.specification

import com.example.yummiziapp.features.user.ui.model.UserDetailsUiModel

sealed interface UserSpecificationUiState {
    data object Loading : UserSpecificationUiState
    data class   Success(val details: UserDetailsUiModel) : UserSpecificationUiState
    data class   Error(val errorMessage: String) : UserSpecificationUiState
}