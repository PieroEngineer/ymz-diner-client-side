package com.example.yummiziapp.features.user.ui.model

import kotlin.uuid.Uuid

data class SelectedRestaurantUiModel(
    val restaurantId: Uuid,
    val distanceKm: Int,
    val restaurantLogoUrl: String,
    val waitingState: PendingRequestUiStatus,
)