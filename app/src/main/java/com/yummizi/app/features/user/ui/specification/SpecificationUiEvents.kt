package com.yummizi.app.features.user.ui.specification

import kotlin.uuid.Uuid

sealed interface SpecificationUiEvents {
    data class NavigateToRestaurantMenu(val restaurantId: Uuid) : SpecificationUiEvents
    data object NavigateToHome : SpecificationUiEvents
    data object NavigateToPreferences : SpecificationUiEvents
    data object NavigateToContact : SpecificationUiEvents
    data class IntentFindLocation(val url: String) : SpecificationUiEvents
}