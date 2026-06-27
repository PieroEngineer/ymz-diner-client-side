package com.example.yummiziapp.core.data.repository

import android.util.Log
import com.example.yummiziapp.core.data.di.IoDispatcher
import com.example.yummiziapp.core.data.local.dao.LoggedUserDao
import com.example.yummiziapp.core.data.local.dao.RequestDao
import com.example.yummiziapp.core.data.local.entities.Request
import com.example.yummiziapp.core.data.local.model.RequestStatusToken
import com.example.yummiziapp.core.data.network.api.RequestApi
import com.example.yummiziapp.core.data.network.model.LeaveQueueDto
import com.example.yummiziapp.core.data.network.model.toDto
//import com.example.yummiziapp.features.request.data.model.QueueStatus
import com.example.yummiziapp.features.request.domain.repository.RequestRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random
import kotlin.uuid.Uuid
import kotlinx.coroutines.NonCancellable

class RequestRepositoryImpl @Inject constructor(
    private val yummiziApi: RequestApi,
    private val loggedUserDao: LoggedUserDao,
    private val requestDao: RequestDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): RequestRepository {

    override suspend fun createQueueRequest(restaurantId: Uuid) = withContext(ioDispatcher) {   //TODO: later use this "...: Result<Unit> = withContext(ioDispatcher)" in others repo's methods and ask to gemini how to use it. It's better
        val userId = loggedUserDao.getCurrentUserId() ?: Uuid.parse("00000000-0000-4000-a000-000000000000")

        // 1. VALIDATION CHECK
        // We check the local DB first. It's the "Source of Truth" for the current UI state.
        val isBlocked = requestDao.containsPendingRestaurant(userId, restaurantId) //TODO: Avoid introducing user ID here because there is only one logged user per phone
        if (isBlocked) {    //TODO: Show in some way that the restaurant already has a pending request for the restaurant
            Log.w("Yummizi", "🚫 Request blocked: User already has a QUEUED or PAID status at this restaurant.")
            ///return@withContext Result.failure(Exception("You already have an active session here."))
            return@withContext
        }

        // 2. PREPARE ENTITY
        val requestId = Uuid.random()   //X Client should generate the request ID
        val newRequest = Request(
            id = requestId,
            restaurantId = restaurantId,
            userId = userId,
            status = if (Random.nextDouble() < 0.7) RequestStatusToken.ACCEPTED else RequestStatusToken.QUEUED, //TODO: Remove the MockUp acceptation
            localCreationTimestamp = ""//TODO: System.currentTimeMillis()
        )

        // 3. OPTIMISTIC UPDATE
        requestDao.insertRequest(newRequest)

        try {
            // 4. NETWORK SYNC
            val response = yummiziApi.createRequest(newRequest.toDto())

            if (response.isSuccessful) {
                Log.d("Yummizi", "✅☁️ Sync successful: $requestId")
                ///Result.success(Unit)
            } else {
                // Server returned an error (e.g. 400 or 500)
                throw Exception("Server rejected the request")
            }
        } catch (e: Exception) {
            // 5. ROLLBACK
            Log.e("Yummizi", "🚩☁️ Cloud failed. Rolling back: $requestId", e)
            requestDao.deleteRequestById(requestId)
            ///Result.failure(e)
        }
    }

    override suspend fun leaveQueue(restaurantId: Uuid) = withContext(ioDispatcher) {
        val userId = loggedUserDao.getCurrentUserId() ?: Uuid.parse("00000000-0000-4000-a000-000000000000")

        // 1. Fetch the existing request to know WHAT to delete and HOW to rollback
        // We need the requestId to tell the Cloud which specific record to kill.
        val currentRequest = requestDao.getRequestByRestaurantAndUser(restaurantId, userId) ?: return@withContext

        try {
            // 2. OPTIMISTIC UPDATE: Remove from Local DB immediately
            // The UI (observing via Flow) will instantly see the "Join" button again.
            requestDao.deleteRequestById(currentRequest.id)

            // 3. NETWORK CALL: Tell the cloud
            // We use the requestId from our 'backup' variable.
            val response = yummiziApi.cancelRequest(
                requestId = currentRequest.id.toString(),
                cancelData = LeaveQueueDto(restaurantId = restaurantId.toString())
            )

            if (!response.isSuccessful) throw Exception("Cloud failed to cancel request")

        } catch (e: Exception) {
            // 4. ROLLBACK: Re-insert the exact request we just deleted
            // The UI will "pop" back to the "Queued" state automatically.
            withContext(NonCancellable){ /// Maybe it goes also in the try
                requestDao.insertRequest(currentRequest)
            }

            Log.e("MenuRepository", "❌ Failed to leave queue, rolling back local state", e)
            throw e
        }
    }

}