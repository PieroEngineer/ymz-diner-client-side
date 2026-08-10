package com.example.yummiziapp.features.user.ui.specification

import kotlin.uuid.Uuid

sealed interface SpecificationUiEvents {
    data class NavigateToRestaurantMenu(val restaurantId: Uuid) : SpecificationUiEvents
    data object NavigateToHome : SpecificationUiEvents
    data object NavigateToPreferences : SpecificationUiEvents
    data object NavigateToContact : SpecificationUiEvents
}