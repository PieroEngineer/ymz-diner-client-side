package com.example.yummiziapp.core.data.local.model

enum class RequestStatusToken {
    IDLE, QUEUED, ACCEPTED, READY, PAID, DECLINED;

    companion object {
        fun fromString(value: String): RequestStatusToken? {
            return entries.find { it.name == value }
        }
    }
}
