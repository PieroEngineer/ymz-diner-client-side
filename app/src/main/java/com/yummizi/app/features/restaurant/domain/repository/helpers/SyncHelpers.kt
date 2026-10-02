package com.yummizi.app.features.restaurant.domain.repository.helpers

import com.yummizi.app.core.data.local.entities.Request
import com.yummizi.app.core.data.local.model.RequestStatusToken
import com.yummizi.app.core.data.network.model.UpdatedRequestStatusDto
import com.yummizi.app.features.restaurant.domain.model.SyncRequestContent
import kotlin.uuid.Uuid

///For each remote request, it ...
//Exists in local and in remote -> update the local status
//Exists in local but not in remote -> remove it
//Doesn't exist in local, but exists in remote -> insert it
//Doesn't exists in both -> do nothing
suspend fun syncRequests(
    localRequests: List<Request>,
    remoteRequests: List<UpdatedRequestStatusDto>,
    ): SyncRequestContent? {
        // Target state filter set for fast O(1) containment check
        val targetStates = setOf("QUEUED", "ACCEPTED")

        // 1. Index remote items matching scope in O(N)
        val relevantRemoteMap = remoteRequests
            .asSequence()
            .filter { it.state in targetStates }
            .associateBy { it.requestId }

        // 2. Index local items matching scope in O(M)
        val relevantLocalMap = localRequests
            .asSequence()
            .filter { it.status.name in targetStates }
            .associateBy { it.requestId }

        val toInsert = ArrayList<Request>(relevantRemoteMap.size)
        val toUpdate = ArrayList<Request>(relevantRemoteMap.size)

        // 3. Process remote items -> Categorize into Insert or Update in O(N)
        for ((requestId, remoteDto) in relevantRemoteMap) {
            val existingLocal = relevantLocalMap[requestId]
            val mappedStatus = RequestStatusToken.valueOf(remoteDto.state)

            if (existingLocal != null) {
                // EXISTS IN LOCAL & REMOTE -> Update local record with remote payload
                toUpdate.add(
                    existingLocal.copy(
                        restaurantId = Uuid.parse(remoteDto.restaurantId),
                        status = mappedStatus,
                        userId = Uuid.parse(remoteDto.userId),
                        onQueuedTimestamp = remoteDto.onQueuedTimestamp,
                        arrivalTimeInMinutes = remoteDto.arrivalTimeInMinutes
                    )
                )
            } else {
                // DOES NOT EXIST IN LOCAL -> Insert new entity
                toInsert.add(
                    Request(
                        requestId = remoteDto.requestId,
                        restaurantId = Uuid.parse(remoteDto.restaurantId),
                        status = mappedStatus,
                        userId = Uuid.parse(remoteDto.userId),
                        onQueuedTimestamp = remoteDto.onQueuedTimestamp,
                        onContinueAcceptanceLocalTimestamp = null,
                        arrivalTimeInMinutes = remoteDto.arrivalTimeInMinutes
                    )
                )
            }
        }

        // 4. Identify local scoped items missing from remote batch -> Delete in O(M)
        val toDeleteIds = relevantLocalMap.keys.filter { id -> id !in relevantRemoteMap }

        // 5. Atomic Batch I/O Execution
        if (toInsert.isNotEmpty() || toUpdate.isNotEmpty() || toDeleteIds.isNotEmpty()) {
            return SyncRequestContent(
                toInsert = toInsert,
                toUpdate = toUpdate,
                toDeleteIds = toDeleteIds
            )
        }
        else{
            return null
        }
}