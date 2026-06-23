package com.example.yummiziapp.core.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.example.yummiziapp.core.data.local.entities.Restaurant
import com.example.yummiziapp.features.request.data.model.QueueStatus

data class RestaurantWithStatus(
    @Embedded val restaurant: Restaurant,
    @ColumnInfo(name = "requestStatus") val status: QueueStatus
)