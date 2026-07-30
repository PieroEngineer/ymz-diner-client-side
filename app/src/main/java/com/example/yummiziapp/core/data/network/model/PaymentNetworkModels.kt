package com.example.yummiziapp.core.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SendOrderReadyToBePaidDto(
    val requestId: String,
    val restaurantId: String,
    val userId: String,
    val userName: String,
    val arrivalTimeInMinutes: Int,
    val groupSize: Int,
    val orderSelections: List<OrderSelectionDto>
)

@Serializable
data class OrderSelectionDto(
    val dishId: String,
    val dishName: String,
    val quantity: Int,
    val historicalUnitPriceInCents: Int
)

@Serializable
data class SendOrderReadyToBePaidResponseDto(
    val message: String,
    val totalPriceInCents: Int
)