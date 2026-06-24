package com.example.yummiziapp.core.data.network.api

import com.example.yummiziapp.core.data.network.model.PreferenceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path

interface PreferenceApi {

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
}