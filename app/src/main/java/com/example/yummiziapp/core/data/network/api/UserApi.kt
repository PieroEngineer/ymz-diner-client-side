package com.example.yummiziapp.core.data.network.api

import com.example.yummiziapp.core.data.network.model.PreferenceDto
import com.example.yummiziapp.core.data.network.model.UserCreationPayload
import com.example.yummiziapp.core.data.network.model.UserDataPayload
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {

    @PUT("users/{userId}/preferences/{prefId}")
    suspend fun savePreference(
        @Path("userId") userId: String,
        @Path("prefId") prefId: String,
        @Body preference: PreferenceDto
    ): Response<Unit>

    @DELETE("users/{userId}/preferences/{prefId}")
    suspend fun deletePreference(
        @Path("userId") userId: String,
        @Path("prefId") prefId: String
    ): Response<Unit>

    @POST("users/{userId}/create")
    suspend fun createUser(
        @Path("userId") userId: String,
        @Body user: UserCreationPayload
    ): Response<Unit>

    @GET("users/{userId}")
    suspend fun getUserData(
        @Path("userId") userId: String
    ): UserDataPayload
}