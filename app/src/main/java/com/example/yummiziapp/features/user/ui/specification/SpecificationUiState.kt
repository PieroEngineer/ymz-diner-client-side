package com.example.yummiziapp.features.user.ui.specification

import com.example.yummiziapp.features.restaurant.ui.model.SelectedRestaurantUiModel
import com.example.yummiziapp.features.user.ui.model.FocusedRestaurantUiModel

sealed interface SpecificationUiState{
    data object Loading : SpecificationUiState
    data class   ListingRestaurants(val restaurants: List<SelectedRestaurantUiModel>) : SpecificationUiState
    data class   FocusingRestaurant(val restaurant: FocusedRestaurantUiModel) : SpecificationUiState
    data class   Error(val errorMessage: String) : SpecificationUiState
}