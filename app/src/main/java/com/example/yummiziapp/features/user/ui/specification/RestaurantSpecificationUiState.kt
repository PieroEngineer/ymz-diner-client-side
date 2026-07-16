package com.example.yummiziapp.features.user.ui.specification

import com.example.yummiziapp.features.user.ui.model.SelectedRestaurantUiModel
import com.example.yummiziapp.features.user.ui.model.FocusedRestaurantUiModel

sealed interface RestaurantSpecificationUiState{
    data object Loading : RestaurantSpecificationUiState
    data class   ListingRestaurants(val restaurants: List<SelectedRestaurantUiModel>) : RestaurantSpecificationUiState
    data class   FocusingRestaurant(val restaurant: FocusedRestaurantUiModel) : RestaurantSpecificationUiState
    data class   Error(val errorMessage: String) : RestaurantSpecificationUiState
}