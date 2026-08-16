package com.example.yummiziapp.core.data.repository

import android.util.Log
import com.example.yummiziapp.core.data.di.IoDispatcher
import com.example.yummiziapp.core.data.local.dao.LoggedUserDao
import com.example.yummiziapp.core.data.local.dao.RequestDao
import com.example.yummiziapp.core.data.local.entities.Request
import com.example.yummiziapp.core.data.local.model.RequestStatusToken
import com.example.yummiziapp.core.data.network.api.RequestApi
import com.example.yummiziapp.core.data.network.model.LeaveQueueDto
import com.example.yummiziapp.core.data.network.model.UpdateStatusDto
import com.example.yummiziapp.core.data.network.model.mappers.mapToGetUpdatedRequestStatusesDto
import com.example.yummiziapp.core.data.network.model.mappers.mapToJoinQueueDto
import com.example.yummiziapp.features.request.domain.repository.RequestRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.uuid.Uuid
import kotlinx.coroutines.NonCancellable
import kotlin.time.Clock

class RequestRepositoryImpl @Inject constructor(
    private val yummiziApi: RequestApi,
    private val loggedUserDao: LoggedUserDao,
    private val requestDao: RequestDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
): RequestRepository {

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
        val requestId = Uuid.random()   // TODO (#521): Remember that client shouldn't generate the request ID. Backend responds with request
        val newRequest = Request(
            requestId = requestId,
            restaurantId = restaurantId,
            userId = loggedUser.id,
            status = RequestStatusToken.QUEUED,
            localCreationTimestamp = System.currentTimeMillis(),    // TODO: Change by "Instant" type
            arrivalTimeInMinutes = arrivalTimeInMinutes,
        )

        // 3. OPTIMISTIC UPDATE
        requestDao.insertRequest(newRequest)

        try {
            // 4. NETWORK SYNC
//X            val response = yummiziApi.createRequest(newRequest.toDto())
            val response = yummiziApi.createRequest(mapToJoinQueueDto(loggedUser, newRequest))

            if (response.isSuccessful) {
                Log.d("Yummizi", "✅☁️ Sync successful: $requestId")
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

    override suspend fun leaveQueue(restaurantId: Uuid) = withContext(ioDispatcher) {

        // 1. Fetch the existing request to know WHAT to delete and HOW to rollback
        // We need the requestId to tell the Cloud which specific record to kill.
        val currentRequest = requestDao.getRequestByRestaurantId(restaurantId) ?: return@withContext

        try {
            // 2. OPTIMISTIC UPDATE: Remove from Local DB immediately
            // The UI (observing via Flow) will instantly see the "Join" button again.
            requestDao.deleteRequestById(currentRequest.requestId)

            // 3. NETWORK CALL: Tell the cloud
            // We use the requestId from our 'backup' variable.
            val response = yummiziApi.cancelRequest(
                requestId = currentRequest.requestId.toString(),
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

    override suspend fun confirmAcceptance(restaurantId: Uuid) = withContext(ioDispatcher) {
        val userId = loggedUserDao.getCurrentUserId() ?: throw IllegalStateException("No logged-in user found")

        //TODO: Check if the transition is valid (e.g. QUEUED -> ACCEPTED)

        // Fetch current and other requests for backup
        val currentRequest = requestDao.getRequestByRestaurantId(restaurantId) ?: return@withContext
        val otherRequests = requestDao.getOtherRequests(currentRequest.requestId)

        try {
            // OPTIMISTIC UPDATE: Update status to READY and remove other requests
            requestDao.confirmAcceptance(currentRequest.requestId, Clock.System.now())  /// In future update, maybe the completly delete won't the best option to get the history of histories. (Maybe just include a DAO function that notify to everyone that there is a READY request)

            // NETWORK CALL
            val response = yummiziApi.updateRequestStatus(
                restaurantId = restaurantId.toString(),
                confirmData = UpdateStatusDto(
                    requestId = currentRequest.requestId.toString(),
                    userId = userId.toString(),
                    newState = RequestStatusToken.READY.name
                )
            )

            if (!response.isSuccessful) throw Exception("Cloud failed to confirm acceptance")

        } catch (e: Exception) {
            // ROLLBACK: Restore everything
            withContext(NonCancellable) { // TODO: Try it without "withContext(NonCancellable)", because now it's in repo /// Gemini: Warning: Do NOT use NonCancellable to start long-running network or database calls in a ViewModel. It is strictly designed for immediate, lightweight cleanup operations in a finally block.
                requestDao.updateRequestStatus(currentRequest.requestId, currentRequest.status)
                if(otherRequests.isNotEmpty()){
                    otherRequests.forEach { requestDao.insertRequest(it) }
                }
            }

            Log.e("RequestRepository", "❌ Failed to confirm acceptance, rolling back local state", e)
            throw e
        }
    }

    override suspend fun fetchUpdatedRequestStatuses() = withContext(ioDispatcher) {
        // 1. Get all the current requests from DAO
        val requests = requestDao.getAllRequests()
        if (requests.isEmpty()) return@withContext

        // 2. Map to DTO
        val dto = mapToGetUpdatedRequestStatusesDto(requests)

        try {
            // 3. Send the result to the API
            val response = yummiziApi.getActiveRequest(dto)

            if (response.isSuccessful) {
                // 4. Update the state of the requests in DAO
                response.body()?.statuses?.forEach { statusDto ->
                    val newStatus = RequestStatusToken.fromString(statusDto.state)
                    if (newStatus != null) {
                        requestDao.updateRequestStatus(
                            requestId = Uuid.parse(statusDto.requestId),
                            status = newStatus
                        )
                    } else {
                        Log.w("RequestRepository", "🚩 Unknown status from cloud: ${statusDto.state}")
                    }
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
                        requestId = currentRequest.requestId.toString(),
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