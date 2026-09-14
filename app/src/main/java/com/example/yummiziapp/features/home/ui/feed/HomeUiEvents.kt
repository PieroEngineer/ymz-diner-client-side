package com.example.yummiziapp.features.home.ui.feed

import kotlin.uuid.Uuid

sealed interface HomeUiEvents {
    data class NavigateToRestaurantSelectionFrame(val restaurantId: Uuid?) : HomeUiEvents
    data class NavigateToRestaurantMenuPreview(val restaurantId: Uuid) : HomeUiEvents
    data class NavigateToReadyRestaurantMenu(val restaurantId: Uuid) : HomeUiEvents
    data class NavigateToPaidRestaurant(val restaurantId: Uuid) : HomeUiEvents
    data object ShowConnectionError : HomeUiEvents
    data object NavigateToPreferences : HomeUiEvents
    data object NavigateToContact : HomeUiEvents
    data class SnackBar(val message: String) : HomeUiEvents
}