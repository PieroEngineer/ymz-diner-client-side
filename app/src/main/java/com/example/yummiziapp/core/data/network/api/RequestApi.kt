package com.example.yummiziapp.core.data.network.api

import com.example.yummiziapp.core.data.network.model.JoinQueueDto
import com.example.yummiziapp.core.data.network.model.CreateRequestResponseDto
import com.example.yummiziapp.core.data.network.model.GetUpdatedRequestStatusesDto
import com.example.yummiziapp.core.data.network.model.GetUpdatedRequestStatusesResponseDto
import com.example.yummiziapp.core.data.network.model.LeaveQueueDto
import com.example.yummiziapp.core.data.network.model.LeaveQueueResponseDto
import com.example.yummiziapp.core.data.network.model.UpdateStatusDto
import com.example.yummiziapp.core.data.network.model.UpdateStatusResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    @POST("requests/active")
    suspend fun getActiveRequest(
        @Body body: GetUpdatedRequestStatusesDto
    ): Response<GetUpdatedRequestStatusesResponseDto>

}