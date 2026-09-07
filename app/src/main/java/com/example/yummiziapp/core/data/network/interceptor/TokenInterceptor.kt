package com.example.yummiziapp.core.data.network.interceptor

import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.kotlin.core.Amplify
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Interceptor's intercept() runs synchronously on OkHttp's background thread pool.
        // We use runBlocking here to safely suspend and wait for Amplify to return
        // the cached JWT ID Token from local storage.
        val token: String? = runBlocking {
            try {
                val session = Amplify.Auth.fetchAuthSession() as? AWSCognitoAuthSession
                // idToken contains user claims (sub, email, custom attributes) used by backends
                session?.userPoolTokensResult?.value?.idToken
            } catch (e: Exception) {
                null
            }
        }

        // Do not send Bearer token when uploading directly to S3
//        if (originalRequest.url.host.contains("s3.amazonaws.com")) {
//            return chain.proceed(originalRequest)
//        }

        val requestBuilder = originalRequest.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")

        // Injects token only if user is logged in
        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}