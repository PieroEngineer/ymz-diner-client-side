package com.yummizi.app.core.data.network.model.mappers

import com.yummizi.app.core.data.local.entities.LoggedUser
import com.yummizi.app.core.data.local.entities.Request
import com.yummizi.app.core.data.network.model.BatchRequestCancellationDto
import com.yummizi.app.core.data.network.model.BatchRequestCancellationItemDto
import com.yummizi.app.core.data.network.model.GetUpdatedRequestStatusesDto
import com.yummizi.app.core.data.network.model.JoinQueueDto
import com.yummizi.app.core.data.network.model.RequestUpdateDto

fun mapToJoinQueueDto(loggedUser: LoggedUser, request: Request): JoinQueueDto {
    return JoinQueueDto(
        restaurantId = request.restaurantId.toString(),
        userId = loggedUser.id.toString(),
        requestId = request.requestId.toString(),
        groupSize = loggedUser.companySize + 1,
        userName = loggedUser.name,
        arrivalTime = request.arrivalTimeInMinutes
    )
}

fun mapToGetUpdatedRequestStatusesDto(requests: List<Request>): GetUpdatedRequestStatusesDto {
    return GetUpdatedRequestStatusesDto(
        requestsToUpdate = requests.map {
            RequestUpdateDto(
                restaurantId = it.restaurantId.toString(),
                requestId = it.requestId.toString()
            )
        }
    )
}

fun mapToBatchRequestCancellationDto(requests: List<Request>): BatchRequestCancellationDto {
    return BatchRequestCancellationDto(
        requestsToCancel = requests.map {
            BatchRequestCancellationItemDto(
                restaurantId = it.restaurantId.toString(),
                requestId = it.requestId.toString()
            )
        }
    )
}