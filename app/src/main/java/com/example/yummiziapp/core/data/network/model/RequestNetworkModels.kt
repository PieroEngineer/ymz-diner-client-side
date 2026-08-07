package com.example.yummiziapp.core.data.network.model

import android.util.Log
import com.example.yummiziapp.core.data.local.entities.RequestUpdate
import com.example.yummiziapp.core.data.local.model.RequestStatusToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class JoinQueueDto(
    val restaurantId: String,
    val userId: String,
    val requestId: String,
    val groupSize: Int,
    val userName: String,
//    val specificities: List<RequestStatusToken>,      // TODO
    val arrivalTime: Int
)

@Serializable
data class CreateRequestResponseDto(
    val message: String,
    val requestId: String,
)

@Serializable
data class GetUpdatedRequestStatusesDto(
    val requestsToUpdate: List<RequestUpdateDto>
)
@Serializable
data class RequestUpdateDto(
    val restaurantId: String,
    val requestId: String
)


@Serializable
data class GetUpdatedRequestStatusesResponseDto(
    val statuses: List<UpdatedRequestStatusDto>
)
@Serializable
data class UpdatedRequestStatusDto(
    val requestId: String,
//    val restaurantId: String,
    val state: String,
//    val userId: String,
//    val groupSize: String,
//    val onQueuedTimestamp: String,
//    val specifics: String,
//    val arrivlaTime: Int
)

@Serializable
data class LeaveQueueDto(
    val restaurantId: String
)

@Serializable
data class LeaveQueueResponseDto(
    val message: String,
//    val onCancellationTimestamp: String  //TODO: Use this timestamp in other DTOs so the client-side can have the online timestamp of the cloud
)

@Serializable
data class UpdateStatusDto( /// It's subject to changes due the observation that I did in the backend
    val requestId: String,
    val userId: String,
    val newState: String
)

@Serializable
data class UpdateStatusResponseDto(
    val message: String,
)

@Serializable
data class RequestStatusMessageDto(
    @SerialName("requestId") val requestId: String,
    @SerialName("newStatus") val newStatus: String
){
    fun toEntity(): RequestUpdate {
        val newLocalStatus = RequestStatusToken.fromString(newStatus) ?: run {
            Log.e("MyActivity", "🚩Invalid status in WebSockets: $newStatus")
            throw IllegalArgumentException("Invalid status: $newStatus")    // TODO: Handle it in another way
        }
        return RequestUpdate(
            requestId = Uuid.parse(requestId),
            newStatus = newLocalStatus
        )
    }
}


/// Remember, the "get details" don't replace the food, but concatenate it