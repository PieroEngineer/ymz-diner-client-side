package com.example.yummiziapp.core.data.local.model

data class RestaurantWithStatus(
    @Embedded val restaurant: Restaurant,
    @ColumnInfo(name = "requestStatus") val status: QueueStatus
)