package com.yummizi.app.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.yummizi.app.core.data.local.entities.Request
import com.yummizi.app.core.data.local.model.RequestStatusToken
//X import com.yummizi.app.features.request.data.model.QueueStatus
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Dao
interface RequestDao {

    // --- REPOSITORY / BACKGROUND ACTIONS ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: Request)

    @Query("DELETE FROM requests WHERE requestId = :requestId")
    suspend fun deleteRequestById(requestId: String)

    // --- UI OBSERVATION (REACTIVE) ---

    /**
     * Observe all active requests for the current user.
     * If the user joins multiple queues, the UI updates automatically.
     */
    @Query("SELECT * FROM requests WHERE userId = (SELECT id FROM logged_user LIMIT 1) ORDER BY localCreationTimestamp DESC")
    fun observeUserRequests(): Flow<List<Request>>

    /**
     * Observe a specific request. Useful for the "Status Screen" of a single restaurant.
     */
    @Query("SELECT * FROM requests WHERE restaurantId = :restaurantId AND userId = (SELECT id FROM logged_user LIMIT 1) LIMIT 1")
    fun observeRequestForRestaurant(restaurantId: Uuid): Flow<Request?>

    @Query("SELECT * FROM requests WHERE restaurantId = :restaurantId LIMIT 1")
    suspend fun getRequestByRestaurantId(restaurantId: Uuid): Request?

    @Query("SELECT * FROM requests WHERE restaurantId = :restaurantId LIMIT 1")
    fun observeRequestByRestaurantId(restaurantId: Uuid): Flow<Request?>

    @Query("SELECT EXISTS(SELECT 1 FROM requests)")
    fun observeIfThereAreRequests(): Flow<Boolean>

    // --- ONE-SHOT QUERIES (LOGIC CHECKS) ---

    @Query("SELECT * FROM requests")
    suspend fun getAllRequests(): List<Request>

    @Query("SELECT * FROM requests WHERE requestId = :requestId")
    suspend fun getRequestById(requestId: String): Request?

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM requests 
            WHERE userId = (SELECT id FROM logged_user LIMIT 1) 
            AND restaurantId = :restaurantId 
            AND status IN ('QUEUED', 'ACCEPTED')
            LIMIT 1
        )
    """)
    suspend fun containsPendingRestaurant(restaurantId: Uuid): Boolean

    @Query("SELECT * FROM requests WHERE status IN ('READY', 'PAY_TRIED') ORDER BY localCreationTimestamp DESC LIMIT 1")
    suspend fun getLatestReadyRequest(): Request?   // TODO: Include an expiration cheking like an attribute or creating a new status in "RequestStatusToken" // Maybe not because the request is locally removed

    @Query("SELECT * FROM requests WHERE status = 'PAID' ORDER BY localCreationTimestamp DESC LIMIT 1")
    suspend fun getLatestPaidRequest(): Request?   // TODO: Include an expiration cheking like an attribute or creating a new status in "RequestStatusToken" // Maybe not because the request is locally removed

    @Query("UPDATE requests SET status = :status, localConfirmationTimestamp = :confirmationTimestamp WHERE requestId = :requestId")
    suspend fun updateRequestStatus(requestId: String, status: RequestStatusToken, confirmationTimestamp: Instant? = null)

    @Query("DELETE FROM requests WHERE requestId != :requestId")
    suspend fun deleteOtherRequests(requestId: String)

    @Query("SELECT * FROM requests WHERE requestId != :requestId")
    suspend fun getOtherRequests(requestId: String): List<Request>

    @Query("DELETE FROM requests")
    suspend fun clearAll()

    @Transaction
    suspend fun confirmAcceptance(requestId: String, confirmationTimestamp: Instant) {
        updateRequestStatus(requestId, RequestStatusToken.READY, confirmationTimestamp)
        deleteOtherRequests(requestId)  // TODO: Consider don't remove them, but just update the status (Assign as "past")
    }
}