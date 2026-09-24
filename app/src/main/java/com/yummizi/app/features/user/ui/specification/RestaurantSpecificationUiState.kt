package com.yummizi.app.features.user.ui.specification

import com.yummizi.app.features.user.ui.model.SelectedRestaurantUiModel
import com.yummizi.app.features.user.ui.model.FocusedRestaurantUiModel

sealed interface RestaurantSpecificationUiState{
    data object Loading : RestaurantSpecificationUiState
    data class   ListingRestaurants(val restaurants: List<SelectedRestaurantUiModel>) : RestaurantSpecificationUiState
    data class   FocusingRestaurant(val restaurant: FocusedRestaurantUiModel) : RestaurantSpecificationUiState
    data class   Error(val errorMessage: String) : RestaurantSpecificationUiState
}