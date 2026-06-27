package com.example.yummiziapp.features.restaurant.ui.model

import com.example.yummiziapp.features.user.ui.model.QueueUiStatus
import kotlin.uuid.Uuid

data class SelectedRestaurantUiModel(
    val restaurantId: Uuid,
    val distanceKm: Int,
    val restaurantLogoUrl: String,
    val waitingState: QueueUiStatus,
)