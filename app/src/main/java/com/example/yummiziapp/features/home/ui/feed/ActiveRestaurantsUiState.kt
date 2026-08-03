package com.example.yummiziapp.features.home.ui.feed

import com.example.yummiziapp.core.data.local.entities.Restaurant
import com.example.yummiziapp.core.data.local.model.RestaurantWithStatus

sealed interface ActiveRestaurantsUiState {
    data object Loading : ActiveRestaurantsUiState
    data class   PendingStatus(val restaurants: List<Restaurant>) : ActiveRestaurantsUiState
    data class   ReadyStatus(val restaurantWithStatus: RestaurantWithStatus) : ActiveRestaurantsUiState
    data class   Error(val errorMessage: String) : ActiveRestaurantsUiState
}