package com.example.yummiziapp.core.data.network.api

import com.example.yummiziapp.core.data.network.model.sendOrderReadyToBePaidDto
import com.example.yummiziapp.core.data.network.model.sendOrderReadyToBePaidResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentApi {
    @POST("payments/confirm")
    suspend fun selectOrder(
//        @Path("requestId") requestId: String, /// Shold I put something in the path?
        @Body readyOrderData: sendOrderReadyToBePaidDto
    ): Response<sendOrderReadyToBePaidResponseDto>
}