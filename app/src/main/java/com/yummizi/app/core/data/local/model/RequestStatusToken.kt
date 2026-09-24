package com.yummizi.app.core.data.local.model

enum class RequestStatusToken {
    IDLE, QUEUED, ACCEPTED, READY, PAY_TRIED/*TODO: This name is temporal for the MVP*/, PAID, DECLINED;
    // TODO: Consider to create the order status, this is where PAY_TRIED goes

    companion object {
        fun fromString(value: String): RequestStatusToken? {
            return entries.find { it.name == value }
        }
    }
}
