package com.yummizi.app.core.data.network.websocket

import io.ably.lib.types.Message
import kotlinx.coroutines.flow.Flow

interface AblyDataSource {
    fun subscribeToChannel(channelName: String): Flow<Message>
    fun getConnectionState(): Flow<String>
}