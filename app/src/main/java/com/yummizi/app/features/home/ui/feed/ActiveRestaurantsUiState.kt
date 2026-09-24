package com.yummizi.app.features.home.ui.feed

import com.yummizi.app.core.data.local.entities.Restaurant
import com.yummizi.app.core.data.local.model.RestaurantWithStatus

sealed interface ActiveRestaurantsUiState {
    data object Loading : ActiveRestaurantsUiState
    data class   PendingStatus(val restaurants: List<Restaurant>) : ActiveRestaurantsUiState
    data class   ReadyStatus(val restaurantWithStatus: RestaurantWithStatus) : ActiveRestaurantsUiState
    data class   Error(val errorMessage: String) : ActiveRestaurantsUiState
}