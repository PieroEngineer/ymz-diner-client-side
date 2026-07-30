package com.example.yummiziapp.features.user.ui.model

import kotlin.uuid.Uuid

data class FocusedRestaurantUiModel(
    val restaurantId: Uuid,
    val restaurantLogoUrl: String,
    val waitingState: PendingRequestUiStatus,
    val distanceKm: Int,
    val name: String,
    val maxWaitingTimeInMinutes: Int
    // TODO: Later include the conditions and company here
)