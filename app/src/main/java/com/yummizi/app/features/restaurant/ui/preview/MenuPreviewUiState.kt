package com.yummizi.app.features.restaurant.ui.preview

import com.yummizi.app.features.restaurant.ui.model.RestaurantInfoUiModel

sealed interface MenuPreviewUiState {
    data object Loading : MenuPreviewUiState
    data class Success(val restaurantInfoUiModel: RestaurantInfoUiModel) : MenuPreviewUiState
    data class Error(val message: String) : MenuPreviewUiState
}