package com.example.yummiziapp.core.data.network.model

import com.example.yummiziapp.core.data.local.entities.Request
import kotlinx.serialization.Serializable

@Serializable
data class CreateRequestDto(
    val restaurantId: String,
    val userId: String,
    val requestId: String
)

@Serializable
data class CreateRequestResponseDto(
    val message: String,
    val requestId: String,
)

@Serializable
data class UpdateStatusDto(
    val requestId: String,
    val restaurantId: String,
    val newState: String
)

@Serializable
data class UpdateRequestResponseDto(
    val status: String,
    val message: String
)

@Serializable
data class LeaveQueueDto(
    val restaurantId: String
)

@Serializable
data class LeaveQueueResponseDto(
    val message: String,
    val onCancellationTimestamp: String  //TODO: Use this timestamp in other DTOs so the client-side can have the online timestamp of the cloud
)

/// Remember, the "get details" don't replace the food, but concatenate it

fun Request.toDto() = CreateRequestDto(
    restaurantId = id.toString(),
    userId = userId.toString(),
    requestId = id.toString()
)