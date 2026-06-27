package com.example.yummiziapp.features.user.ui.model

import com.example.yummiziapp.features.request.data.model.QueueStatus

data class FocusedRestaurantUiModel(
    val restaurantLogoUrl: String,
    val waitingState: QueueStatus,
    val distanceKm: Int,
    val name: String,
    val maxWaitingTimeInMinutes: Int
    // TODO: Later include the conditions and company here
)