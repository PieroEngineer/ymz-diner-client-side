package com.example.yummiziapp.features.restaurant.ui.model

import com.example.yummiziapp.features.request.data.model.QueueStatus
import kotlin.uuid.Uuid

data class SelectableUiModel(
    val restaurantId: Uuid,
    val distanceKm: Int,
    val restaurantLogoUrl: String,
    val waitingState: QueueStatus,
)