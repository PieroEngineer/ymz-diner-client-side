package com.example.yummiziapp.features.user.ui.model

import com.example.yummiziapp.features.restaurant.ui.model.SelectableUiModel

data class SelectedUiModel(
    val firstParameters: SelectableUiModel,
    val distanceKm: Float,
    val name: String,
    val maxWaitingTimeInMinutes: Int
)