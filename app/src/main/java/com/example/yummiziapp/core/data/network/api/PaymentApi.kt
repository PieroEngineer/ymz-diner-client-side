package com.example.yummiziapp.core.data.network.api

import com.example.yummiziapp.core.data.network.model.SendOrderReadyToBePaidDto
import com.example.yummiziapp.core.data.network.model.SendOrderReadyToBePaidResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApi {
    @POST("payments/confirm")
    suspend fun selectOrder(
//        @Path("requestId") requestId: String, /// Shold I put something in the path?
        @Body readyOrderData: SendOrderReadyToBePaidDto
    ): Response<SendOrderReadyToBePaidResponseDto>
}