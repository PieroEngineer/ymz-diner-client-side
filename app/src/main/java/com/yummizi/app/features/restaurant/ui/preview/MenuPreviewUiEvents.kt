package com.yummizi.app.features.restaurant.ui.preview

import kotlin.uuid.Uuid

sealed interface MenuPreviewUiEvents {
    data class NavigateToFocusedRestaurant(val restaurantId: Uuid) : MenuPreviewUiEvents
    data class NavigateToReadyRestaurantMenu(val restaurantId: Uuid) : MenuPreviewUiEvents
    data class NavigateToPaidRestaurant(val restaurantId: Uuid) : MenuPreviewUiEvents
    data object NavigateToHome : MenuPreviewUiEvents
    data object NavigateToYou : MenuPreviewUiEvents
    data object NavigateToContact : MenuPreviewUiEvents
    data class IntentFindLocation(val url: String) : MenuPreviewUiEvents
}