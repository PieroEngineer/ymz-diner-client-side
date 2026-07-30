package com.example.yummiziapp.core.data.network.model.mappers

import com.example.yummiziapp.core.data.local.entities.OrderSelection
import com.example.yummiziapp.core.data.network.model.OrderSelectionDto
import com.example.yummiziapp.core.data.network.model.SendOrderReadyToBePaidDto
import kotlin.String
import kotlin.uuid.Uuid

fun OrderSelection.toDto(): OrderSelectionDto {
    return OrderSelectionDto(
        dishId = dishId.toString(),
        quantity = quantity,
        dishName = dishName,
        historicalUnitPriceInCents = historicalUnitPriceInCents
    )
}

fun List<OrderSelection>.toDtoList(): List<OrderSelectionDto> {
    return this.map { it.toDto() }
}

fun createSendOrderDto(
    requestId: Uuid,
    restaurantId: Uuid,
    userId: Uuid,
    userName: String,
    arrivalTime: Int,
    groupSize: Int,
    selections: List<OrderSelectionDto>
): SendOrderReadyToBePaidDto {
    return SendOrderReadyToBePaidDto(
        requestId = requestId.toString(),
        restaurantId = restaurantId.toString(),
        userId = userId.toString(),
        userName = userName,
        arrivalTimeInMinutes = arrivalTime,
        groupSize = groupSize,
        orderSelections = selections
    )
}
