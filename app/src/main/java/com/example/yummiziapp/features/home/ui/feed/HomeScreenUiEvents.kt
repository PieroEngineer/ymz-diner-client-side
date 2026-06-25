package com.example.yummiziapp.features.home.ui.feed

import kotlin.uuid.Uuid

sealed interface HomeScreenUiEvents {
    data object NavigateToRestaurantSelectionFrame : HomeScreenUiEvents
    data class NavigateToRestaurantMenuPreview(val restaurantId: Uuid) : HomeScreenUiEvents
    data object ShowConnectionError : HomeScreenUiEvents
    data object NavigateToPreferences : HomeScreenUiEvents
}