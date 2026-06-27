package com.example.yummiziapp.features.restaurant.ui.model

import com.example.yummiziapp.features.user.ui.model.RequestUiStatus
import kotlin.uuid.Uuid

data class SelectedRestaurantUiModel(
    val restaurantId: Uuid,
    val distanceKm: Int,
    val restaurantLogoUrl: String,
    val waitingState: RequestUiStatus,
)