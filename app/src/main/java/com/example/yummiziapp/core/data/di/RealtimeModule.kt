package com.example.yummiziapp.core.data.di

import com.example.yummiziapp.BuildConfig
import com.example.yummiziapp.core.data.network.websocket.AblyDataSource
import com.example.yummiziapp.core.data.network.websocket.AblyDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ably.lib.realtime.AblyRealtime
import io.ably.lib.types.ClientOptions
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RealtimeModule {

    @Provides
    @Singleton
    fun provideClientOptions(): ClientOptions {
        return ClientOptions().apply {
            // 🛡️ API Key Dev Configuration (Direct connection)
            // ⚠️ WARNING: Raw API keys inside binaries should only be used in early dev environments.
            key = BuildConfig.ABLY_API_KEY

            // ================================================================
            // 🔒 HINT: HOW TO DEPLOY SECURE PRODUCTION TOKEN AUTHENTICATION LATER:
            // =================================================================
            // To transition away from local API keys, comment out 'key' above and replace with:
            /*
            authUrl = "https://your-backend-api.com/v1/realtime/ably-token"
            authMethod = "POST"
            authHeaders = mapOf("Authorization" to "Bearer YOUR_APP_USER_JWT")
            clientId = "user-session-or-uuid-123" // Required for secure presence identification
            */
            clientId = "customer-mobile"    // TODO: In production, you should set this dynamically to the authenticated user's unique ID (e.g., userId or JWT token claim) so Ably can identify individual users across presence and device tracking.

            // ⚡ Client optimization: Disable queueing events to minimize memory footprints for subscribe-only clients
            queueMessages = false
            autoConnect = true
        }
    }

    @Provides
    @Singleton
    fun provideAblyRealtime(options: ClientOptions): AblyRealtime {
        return AblyRealtime(options)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RealtimeBindingModule {

    @Binds
    @Singleton
    abstract fun bindAblyDataSource(
        impl: AblyDataSourceImpl
    ): AblyDataSource
}