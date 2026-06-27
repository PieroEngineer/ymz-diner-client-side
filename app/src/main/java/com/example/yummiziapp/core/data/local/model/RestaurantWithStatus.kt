package com.example.yummiziapp.core.data.local.model

import androidx.room.Embedded
import com.example.yummiziapp.core.data.local.entities.Restaurant

data class RestaurantWithStatus(
    @Embedded val restaurant: Restaurant,
//    @ColumnInfo(name = "requestStatus") val status: QueueStatusToken
    val requestStatus: RequestStatusToken
)