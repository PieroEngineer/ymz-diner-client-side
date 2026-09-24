package com.yummizi.app.core.data.local.model

import androidx.room.Embedded
import com.yummizi.app.core.data.local.entities.Restaurant

data class RestaurantWithStatus(
    @Embedded val restaurant: Restaurant,
//    @ColumnInfo(name = "requestStatus") val status: QueueStatusToken
    val requestStatus: RequestStatusToken
)