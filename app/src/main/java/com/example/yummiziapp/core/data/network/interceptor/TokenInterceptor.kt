package com.example.yummiziapp.core.data.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenInterceptor @Inject constructor() : Interceptor {    // Continue here: change "remote" for "network" and follow Gemini instructions
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Placeholder: Fetch your user session token here later
        val token: String? = null

        val requestBuilder = originalRequest.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")

        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}