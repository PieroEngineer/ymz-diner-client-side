package com.example.yummiziapp.features.request.domain.repository

import kotlin.uuid.Uuid

interface RequestRepository {

    /**
     * Creates a new queue request.
     * 1. Generates a local requestId.
     * 2. Optimistically updates the Restaurant's requestId and User's queued list.
     * 3. Syncs with the Cloud (AWS).
     * 4. Performs a rollback if the network fails.
     */
    suspend fun createQueueRequest(restaurantId: Uuid)

//    /**
//     * Transitions a restaurant between state lists (Queued -> Paid, etc).
//     * Uses atomicity to ensure the ID is removed from the old list
//     * and added to the new one simultaneously.
//     */
//    suspend fun updateRestaurantStatus(
//        restaurantId: Uuid,
//        requestId: Uuid,
//        newStatus: QueueStatusToken
//    )

    suspend fun leaveQueue(restaurantId: Uuid)

}