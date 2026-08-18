package com.example.yummiziapp.core.data.di

import com.example.yummiziapp.BuildConfig
import com.example.yummiziapp.core.data.network.api.ContactApi
import com.example.yummiziapp.core.data.network.api.PaymentApi
import com.example.yummiziapp.core.data.network.api.PreferenceApi
import com.example.yummiziapp.core.data.network.api.RequestApi
import com.example.yummiziapp.core.data.network.api.RestaurantApi
import com.example.yummiziapp.core.data.network.interceptor.TokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRestaurantApi(retrofit: Retrofit): RestaurantApi {
        return retrofit.create(RestaurantApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRequestApi(retrofit: Retrofit): RequestApi {
        return retrofit.create(RequestApi::class.java)
    }

    @Provides
    @Singleton
    fun providePreferenceApi(retrofit: Retrofit): PreferenceApi {
        return retrofit.create(PreferenceApi::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentApi(retrofit: Retrofit): PaymentApi {
        return retrofit.create(PaymentApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFeedbackApi(retrofit: Retrofit): ContactApi {
        return retrofit.create(ContactApi::class.java)
    }


    @Provides
    @Singleton
    fun provideJsonConfiguration(): Json {
        return Json {
            ignoreUnknownKeys = true // Prevents crashes if backend adds new fields without telling you
            coerceInputValues = true // Provides safe defaults for missing primitive fields
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenInterceptor: TokenInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY // Detailed logs during local development
            } else {
                HttpLoggingInterceptor.Level.NONE // Dead silent in production for maximum security
            }
        }

        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(tokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitClient(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
// FIXME:           .baseUrl(BuildConfig.BASE_URL) // Pulls safely from the environment configuration we set up earlier!
            .baseUrl("http://localhost:4566/_aws/execute-api/h43ofttmj1/prod/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}