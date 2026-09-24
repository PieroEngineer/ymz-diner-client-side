package com.yummizi.app.core.data.network.interceptor

import android.util.Log
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

        // Robust S3/Presigned URL detection:
        // We check for bucket name, S3 host, OR the presence of 'X-Amz-Algorithm'
        val urlString = originalRequest.url.toString()
        val isS3Request = urlString.contains("yummizi-media-storage", ignoreCase = true) ||
                urlString.contains("s3.amazonaws.com", ignoreCase = true) ||
                originalRequest.url.queryParameter("X-Amz-Algorithm") != null

        Log.d("MainActivity", "🔎🔑 Request URL: $urlString")
        
        if (isS3Request) {
            Log.d("MainActivity", "🔎🔑 S3/Presigned request detected. Skipping auth injection.")
            return chain.proceed(originalRequest)
        }

        Log.d("MainActivity", "🔎🔑 Normal API request detected.")

        val requestBuilder = originalRequest.newBuilder()

        // Only add default JSON headers if they are not already present.
        // This prevents overwriting MIME types for uploads if detection fails.
        if (originalRequest.header("Content-Type") == null) {
            requestBuilder.header("Content-Type", "application/json")
        }
        if (originalRequest.header("Accept") == null) {
            requestBuilder.header("Accept", "application/json")
        }

        // Injects token only if user is logged in
        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}