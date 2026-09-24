package com.yummizi.app.core.data.network.api

import com.yummizi.app.core.data.network.model.RestaurantPaymentResponseDto
import com.yummizi.app.core.data.network.model.SendOrderReadyToBePaidDto
import com.yummizi.app.core.data.network.model.SendOrderReadyToBePaidResponseDto
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url

interface PaymentApi {
    @POST("payments/confirm")
    suspend fun selectOrder(
//        @Path("requestId") requestId: String, /// Shold I put something in the path?
        @Body readyOrderData: SendOrderReadyToBePaidDto
    ): Response<SendOrderReadyToBePaidResponseDto>

    @PUT
    suspend fun uploadToPresignedUrl(
        @Url uploadUrl: String,
        @Header("Content-Type") contentType: String,
        @Body fileBody: RequestBody
    ): Response<Unit>

}