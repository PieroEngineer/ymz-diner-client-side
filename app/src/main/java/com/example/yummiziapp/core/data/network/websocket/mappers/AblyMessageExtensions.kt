package com.example.yummiziapp.core.data.network.websocket.mappers

import io.ably.lib.types.Message

/**
 * 🚀 High-performance payload parser for Ably Messages.
 * Handles String, ByteArray (binary UTF-8), and org.json.JSONObject representations.
 */
fun Message.toRawJsonString(): String? {
    return when (val payload = this.data) {
        is String -> payload
        is ByteArray -> payload.decodeToString()
        else -> payload?.toString()
    }
}