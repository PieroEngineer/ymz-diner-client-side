package com.example.yummiziapp.features.home.ui.feed

import com.example.yummiziapp.core.data.local.entities.Restaurant

sealed interface ActiveRestaurantsUiState {
    data object Loading : ActiveRestaurantsUiState
    data class   PendingStatus(val restaurants: List<Restaurant>) : ActiveRestaurantsUiState
    data class   ReadyStatus(val restaurant: Restaurant) : ActiveRestaurantsUiState
    data class   Error(val errorMessage: String) : ActiveRestaurantsUiState
}