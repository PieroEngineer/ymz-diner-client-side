package com.example.yummiziapp.core.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.yummiziapp.core.data.local.entities.Order
import com.example.yummiziapp.core.data.local.entities.OrderSelection

data class OrderWithSelectionsRelation(
    @Embedded
    val order: Order,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId"
    )
    val selections: List<OrderSelection>
)
