package com.example.yummiziapp.features.home.ui.feed

import kotlin.uuid.Uuid

sealed interface HomeScreenUiEvents {
    data class NavigateToRestaurantSelectionFrame(val restaurantId: Uuid?) : HomeScreenUiEvents
    data class NavigateToRestaurantMenuPreview(val restaurantId: Uuid) : HomeScreenUiEvents
    data object ShowConnectionError : HomeScreenUiEvents
    data object NavigateToPreferences : HomeScreenUiEvents
}