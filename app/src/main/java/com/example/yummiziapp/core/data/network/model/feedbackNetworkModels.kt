package com.example.yummiziapp.core.data.network.model

import kotlinx.serialization.Serializable


@Serializable
data class SendFinalExperienceFeedbackDto(
    val userId: String,
    val requestId: String,
    val conformDegree: String,
    val description: String
)

@Serializable
data class SendFinalExperienceFeedbackResponseDto(
    val message: String
)
