package com.example.yummiziapp.core.data.network.model

import kotlinx.serialization.Serializable


@Serializable
data class SendFeedbackDto(
    val userId: String,
    val requestId: String,
    val conformDegree: String,
    val description: String
)

@Serializable
data class SendFeedbackResponseDto(
    val message: String
)

@Serializable
data class SendRestaurantInfoDto(
    val userId: String,
    val restaurantName: String,
    val restaurantContact: String,
    val restaurantComment: String
)

@Serializable
data class SendRestaurantInfoResponseDto(
    val message: String
)
