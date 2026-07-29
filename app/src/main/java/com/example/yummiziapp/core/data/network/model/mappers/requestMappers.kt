package com.example.yummiziapp.core.data.network.model.mappers

import com.example.yummiziapp.core.data.local.entities.LoggedUser
import com.example.yummiziapp.core.data.local.entities.Request
import com.example.yummiziapp.core.data.network.model.JoinQueueDto

fun mapToJoinQueueDto(loggedUser: LoggedUser, request: Request): JoinQueueDto{
    return JoinQueueDto(
        restaurantId = request.restaurantId.toString(),
        userId = loggedUser.id.toString(),
        requestId = request.requestId.toString(),
        groupSize = loggedUser.companySize,
        userName = loggedUser.name,
        arrivalTime = request.arrivalTimeInMinutes
    )
}
