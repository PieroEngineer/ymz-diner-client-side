package com.yummizi.app.core.data.network.websocket

import android.util.Log
import io.ably.lib.realtime.AblyRealtime
import io.ably.lib.realtime.Channel
import io.ably.lib.realtime.ConnectionStateListener
import io.ably.lib.types.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AblyDataSourceImpl @Inject constructor(
    private val ablyRealtime: AblyRealtime
) : AblyDataSource {

    // 🚀 We wrap Ably's traditional callback structure into a clean, cold Flow API
    override fun subscribeToChannel(channelName: String): Flow<Message> = callbackFlow {
        val channel = ablyRealtime.channels.get(channelName)

        val listener = Channel.MessageListener { message ->
            Log.d("AblyDataSource", "📩 Message received on $channelName: ${message.data}")
            // Try sending the socket packet.
            // In case of backpressure overflow, trySend protects the UI thread from blockages.
            trySend(message).isSuccess
        }

        try {
            channel.subscribe(listener)
            Log.d("AblyDataSource", "✅ Successfully subscribed to Ably channel: $channelName")
        } catch (e: Exception) {
            close(e)
            Log.e("AblyDataSource", "❌ Failed subscribing to channel: $channelName", e)
        }

        // 🛡️ CRITICAL: When the Flow collector cancels (e.g. viewmodel dies or user goes off screen),
        // we MUST release the listener to avoid severe client-side memory and socket leaks.
        awaitClose {
            Log.d("AblyDataSource", "🧹 Cleaning up and unsubscribing from $channelName")
            channel.unsubscribe(listener)
        }
    }

    override fun getConnectionState(): Flow<String> = callbackFlow {
        val listener = ConnectionStateListener { stateChange ->
            trySend(stateChange.current.name)
        }

        ablyRealtime.connection.on(listener)
        trySend(ablyRealtime.connection.state.name) // Initial state emit

        awaitClose {
            ablyRealtime.connection.off(listener)
        }
    }
}