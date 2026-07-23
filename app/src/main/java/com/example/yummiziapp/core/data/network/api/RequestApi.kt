package com.example.yummiziapp.core.data.network.api

import com.example.yummiziapp.core.data.network.model.JoinQueueDto
import com.example.yummiziapp.core.data.network.model.CreateRequestResponseDto
import com.example.yummiziapp.core.data.network.model.LeaveQueueDto
import com.example.yummiziapp.core.data.network.model.LeaveQueueResponseDto
import com.example.yummiziapp.core.data.network.model.UpdateStatusDto
import com.example.yummiziapp.core.data.network.model.UpdateStatusResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface RequestApi {

    @POST("requests/{requestId}/cancel")
    suspend fun cancelRequest(
        @Path("requestId") requestId: String,
        @Body cancelData: LeaveQueueDto
    ): Response<LeaveQueueResponseDto>

    @POST("requests/create")
    suspend fun createRequest(
        @Body body: JoinQueueDto
    ): Response<CreateRequestResponseDto>

    @POST("requests/{restaurantId}/update")
    suspend fun confirmAcceptance(
        @Path("restaurantId") restaurantId: String,
        @Body confirmData: UpdateStatusDto,
    ): Response<UpdateStatusResponseDto>

}