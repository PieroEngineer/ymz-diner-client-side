package com.example.yummiziapp.core.data.network.api

import com.example.yummiziapp.core.data.network.model.SendFeedbackDto
import com.example.yummiziapp.core.data.network.model.SendFeedbackResponseDto
import com.example.yummiziapp.core.data.network.model.SendRestaurantInfoDto
import com.example.yummiziapp.core.data.network.model.SendRestaurantInfoResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ContactApi {
    @POST("contacts/feedback")
    suspend fun sendFeedback(
        @Body feedbackData: SendFeedbackDto
    ): Response<SendFeedbackResponseDto>

    @POST("contacts/restaurant")
    suspend fun sendRestaurantInfo(
        @Body restaurantData: SendRestaurantInfoDto
    ): Response<SendRestaurantInfoResponseDto>
}