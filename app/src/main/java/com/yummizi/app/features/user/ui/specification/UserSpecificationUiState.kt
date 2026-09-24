package com.yummizi.app.features.user.ui.specification

import com.yummizi.app.features.user.ui.model.UserDetailsUiModel

sealed interface UserSpecificationUiState {
    data object Loading : UserSpecificationUiState
    data class   Success(val details: UserDetailsUiModel) : UserSpecificationUiState
    data class   Error(val errorMessage: String) : UserSpecificationUiState
}