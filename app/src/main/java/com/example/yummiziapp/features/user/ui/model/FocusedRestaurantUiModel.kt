package com.example.yummiziapp.features.user.ui.model

data class FocusedRestaurantUiModel(
    val restaurantLogoUrl: String,
    val waitingState: RequestUiStatus,
    val distanceKm: Int,
    val name: String,
    val maxWaitingTimeInMinutes: Int
    // TODO: Later include the conditions and company here
)