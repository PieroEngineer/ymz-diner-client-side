package com.yummizi.app.core.data.repository

import android.util.Log
import com.yummizi.app.core.data.di.IoDispatcher
import com.yummizi.app.core.data.local.dao.LoggedUserDao
import com.yummizi.app.core.data.local.dao.RequestDao
import com.yummizi.app.core.data.local.entities.Request
import com.yummizi.app.core.data.local.model.RequestStatusToken
import com.yummizi.app.core.data.network.api.RequestApi
import com.yummizi.app.core.data.network.model.LeaveQueueDto
import com.yummizi.app.core.data.network.model.UpdateStatusDto
import com.yummizi.app.core.data.network.model.mappers.mapToBatchRequestCancellationDto
import com.yummizi.app.core.data.network.model.mappers.mapToGetUpdatedRequestStatusesDto
import com.yummizi.app.core.data.network.model.mappers.mapToJoinQueueDto
import com.yummizi.app.features.request.domain.repository.RequestRepository
import com.yummizi.app.features.restaurant.domain.repository.helpers.syncPendingRequests
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.uuid.Uuid
import kotlinx.coroutines.NonCancellable
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

class RequestRepositoryImpl @Inject constructor(
    private val yummiziApi: RequestApi,
    private val loggedUserDao: LoggedUserDao,
    private val requestDao: RequestDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
): RequestRepository {

    override suspend fun deleteExpiredPendingRequests() = requestDao.deleteExpiredPendingRequests(System.currentTimeMillis())//TODO: Clock.System.now())

    override suspend fun getRequestStatusByRestaurantId(restaurantId: Uuid): RequestStatusToken {

        val request = requestDao.getRequestByRestaurantId(restaurantId) // TODO: See if it's valid to create a DAO function that returns the required "RequestStatusToken" directly (In DAO "@Query("SELECT COALESCE((SELECT status FROM requests WHERE restaurantId = :restaurantId LIMIT 1), 'IDLE')")")

        return request?.status ?: RequestStatusToken.IDLE
    }

    override fun observeRequestByRestaurantId(restaurantId: Uuid) = requestDao.observeRequestByRestaurantId(restaurantId)

    override fun observeIfThereAreRequests() = requestDao.observeIfThereAreRequests()
    
    override suspend fun createQueueRequest(restaurantId: Uuid, arrivalTimeInMinutes: Int) = withContext(ioDispatcher) {   //TODO: later use this "...: Result<Unit> = withContext(ioDispatcher)" in others repo's methods and ask to gemini how to use it. It's better

        val loggedUser = loggedUserDao.getUser()

        if(loggedUser == null){
            Log.w("MyActivity", "🚩  User entity wasn't reached")
            return@withContext
        }

        // 1. VALIDATION CHECK
        // We check the local DB first. It's the "Source of Truth" for the current UI state.
        val isBlocked = requestDao.containsPendingRestaurant(restaurantId) //TODO: Avoid introducing user ID here because there is only one logged user per phone
        if (isBlocked) {    //TODO: Show in some way that the restaurant already has a pending request for the restaurant
            Log.w("MyActivity", "🚫 Request blocked: User already has a QUEUED or PAID status at this restaurant.")
            ///return@withContext Result.failure(Exception("You already have an active session here."))
            return@withContext
        }

        // 2. PREPARE ENTITY
        val requestId = "${Clock.System.now() - 5.hours /* TODO: Change this for a proper time zone function */}_${Random.nextInt(1000, 10000)}"   // TODO (#521): Client shouldn't generate the request ID. Backend responds with request
        val newRequest = Request(
            requestId = requestId,
            restaurantId = restaurantId,
            userId = loggedUser.id,
            status = RequestStatusToken.QUEUED,
//X            creationTimestamp = System.currentTimeMillis(),    // TODO: Change by "Instant" type
            arrivalTimeInMinutes = arrivalTimeInMinutes,
        )

        // 3. OPTIMISTIC UPDATE
        Log.d("MainActivity", "🔎The request is about to be inserted in the DB: $newRequest")
        requestDao.insertRequest(newRequest)

        try {
            // 4. NETWORK SYNC
            val response = yummiziApi.createRequest(mapToJoinQueueDto(loggedUser, newRequest))

            if (response.isSuccessful) {
                Log.d("Yummizi", "✅☁️ Sync successful: $requestId")

                val remoteTimestamp = response.body()?.onQueuedTimestamp?: return@withContext

                requestDao.updateRequestDatetimeById(requestId, remoteTimestamp)

                // TODO (#521): Here the request ID should be returned from the Cloud and subscribe to the request's channel.
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

    override suspend fun clearAll() = requestDao.clearAll() // TODO: This function should have a logic to use different API functions: A function when there is only one request, and another when there are more.

    override suspend fun cancelRequest(restaurantId: Uuid) = withContext(ioDispatcher) {

        val userId = loggedUserDao.getCurrentUserId() ?: throw IllegalStateException("No logged-in user found")

        // 1. Fetch the existing request to know WHAT to delete and HOW to rollback
        // We need the requestId to tell the Cloud which specific record to kill.
        val currentRequest = requestDao.getRequestByRestaurantId(restaurantId) ?: return@withContext

        try {
            // 2. OPTIMISTIC UPDATE: Remove from Local DB immediately
            // The UI (observing via Flow) will instantly see the "Join" button again.
            requestDao.deleteRequestById(currentRequest.requestId)

            Log.d("Yummizi", "🔎  currentRequest.requestId.toString(): ${currentRequest.requestId}")
            Log.d("Yummizi", "🔎  restaurantId.toString(): $restaurantId")
            Log.d("Yummizi", "🔎  userId.toString(): $userId")

            // 3. NETWORK CALL: Tell the cloud
            // We use the requestId from our 'backup' variable.
            val response = yummiziApi.cancelRequest(
                requestId = currentRequest.requestId,
                cancelData = LeaveQueueDto(
                    restaurantId = restaurantId.toString(),
                    userId = userId.toString(),
                    actor = "USER"
                )
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

    override suspend fun confirmAcceptance(restaurantId: Uuid): Result<Unit> = withContext(ioDispatcher) {
        val userId = loggedUserDao.getCurrentUserId() ?: throw IllegalStateException("No logged-in user found")

        //TODO: Check if the transition is valid (e.g. QUEUED -> ACCEPTED)

        // Fetch current and other requests for backup
        val currentRequest = requestDao.getRequestByRestaurantId(restaurantId) ?: return@withContext Result.failure(Exception("No request found"))
        val otherRequests = requestDao.getOtherRequests(currentRequest.requestId)

        Log.d("RequestRepository", "🔎  The other requests are $otherRequests")

        try {

            // NETWORK CALL
            val updateResponse = yummiziApi.updateRequestStatus(
                restaurantId = restaurantId.toString(),
                confirmData = UpdateStatusDto(
                    requestId = currentRequest.requestId,
                    userId = userId.toString(),
                    newState = RequestStatusToken.READY.name
                )
            )

            if (!updateResponse.isSuccessful) throw Exception("Cloud failed to confirm acceptance")

            Log.d("RequestRepository", "✅☁️ Update successful: ${currentRequest.requestId}")

            val batchCancellationResponse = yummiziApi.cancelRequests(
                mapToBatchRequestCancellationDto(otherRequests)
            )

            if (!batchCancellationResponse.isSuccessful) throw Exception("Cloud failed to do the batch cancellation")

            Log.d("RequestRepository", "✅☁️ Batch cancellation successful")

            val onReadyTimestamp = updateResponse.body()?.onUpdateRequestStateTimestamp?: return@withContext Result.failure(Exception(" No timestamp found "))

            // Update status to READY and remove other requests
            requestDao.confirmAcceptance(currentRequest.requestId, Instant.fromEpochMilliseconds(onReadyTimestamp))  /// In future update, maybe the completly delete won't the best option to get the history of histories. (Maybe just include a DAO function that notify to everyone that there is a READY request)

            Result.success(Unit)

        } catch (e: Exception) {

            Log.e("RequestRepository", "❌ Failed to confirm acceptance", e)
            Result.failure(e)
        }
    }

    override suspend fun fetchUpdatedRequestStatuses() = withContext(ioDispatcher) {
        // 1. Get all the current requests from DAO
        val localRequests = requestDao.getAllRequests()
        if (localRequests.isEmpty()) return@withContext

        // 2. Map to DTO
        val dto = mapToGetUpdatedRequestStatusesDto(localRequests)  // TODO: Change this appraach because backend will change later to GET requets.

        try {
            // 3. Send the result to the API
            val response = yummiziApi.getActiveRequest(dto)

            if (response.isSuccessful) {
                /// Consider increase the performance of the following lines
                val remoteActiveRequests = response.body()?.activeRequests?: return@withContext

                val syncRequestContent = syncPendingRequests(
                    localRequests = localRequests,
                    remoteRequests = remoteActiveRequests
                )
                if( syncRequestContent != null){
                    requestDao.syncRequestsTransaction(syncRequestContent)
                }

            } else {
                Log.e("RequestRepository", "🚩 Cloud failed to return updated statuses")
            }
        } catch (e: Exception) {
            Log.e("RequestRepository", "🚩 Error fetching updated statuses from cloud", e)
        }
    }

    override suspend fun notifyAttemptOfPayment(restaurantId: Uuid): Result<Unit>{

        val readyRequest = requestDao.getLatestReadyRequest()
            ?: throw IllegalStateException("No READY request found when confirming an order")

        val userId = loggedUserDao.getCurrentUserId() ?: throw IllegalStateException("No logged-in user found")

        val currentRequest = requestDao.getRequestByRestaurantId(restaurantId) ?: return Result.failure(Exception("No request found"))

        Log.d("RequestRepository", "🔎☝️  About send the notification to the restaurant: current request status is ${currentRequest.status}")

        try{
            if(currentRequest.status == RequestStatusToken.READY){

                requestDao.updateRequestStatus(readyRequest.requestId, RequestStatusToken.PAY_TRIED)

                Log.d("RequestRepository", "🔎☝️  Request is in ready status so send confirmation")

                yummiziApi.updateRequestStatus(
                    restaurantId = restaurantId.toString(),
                    confirmData = UpdateStatusDto(
                        requestId = currentRequest.requestId,
                        userId = userId.toString(),
                        newState = RequestStatusToken.PAY_TRIED.name
                    )
                )

            }
            return Result.success(Unit)
        }
        catch(e: Exception){
            requestDao.updateRequestStatus(readyRequest.requestId, RequestStatusToken.READY)    /// Won't the navigation skip this line?
            Log.e("RequestRepository", "🚩 Error sending notification to the restaurant", e)
            return Result.failure(e)
        }
    }
}