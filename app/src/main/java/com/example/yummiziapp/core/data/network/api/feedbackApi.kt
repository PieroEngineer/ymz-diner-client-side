package com.example.yummiziapp.core.data.network.api

import com.example.yummiziapp.core.data.network.model.SendFinalExperienceFeedbackDto
import com.example.yummiziapp.core.data.network.model.SendFinalExperienceFeedbackResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FeedbackApi {

    @POST("feedbacks/final")
    suspend fun sendFinalExperienceFeedback(
        @Body feedbackData: SendFinalExperienceFeedbackDto
    ): Response<SendFinalExperienceFeedbackResponseDto>

}