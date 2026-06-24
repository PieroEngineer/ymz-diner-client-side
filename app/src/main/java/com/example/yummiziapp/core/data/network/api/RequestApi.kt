package com.example.yummiziapp.core.data.network.api

import com.example.yummiziapp.core.data.network.model.CreateRequestDto
import com.example.yummiziapp.core.data.network.model.CreateRequestResponseDto
import com.example.yummiziapp.core.data.network.model.LeaveQueueDto
import com.example.yummiziapp.core.data.network.model.LeaveQueueResponseDto
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
        @Body body: CreateRequestDto
    ): Response<CreateRequestResponseDto>
}