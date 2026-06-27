package com.example.yummiziapp.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yummiziapp.core.data.local.entities.Request
import com.example.yummiziapp.core.data.local.model.RequestStatusToken
//X import com.example.yummiziapp.features.request.data.model.QueueStatus
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface RequestDao {

    // --- REPOSITORY / BACKGROUND ACTIONS ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: Request)

    @Query("DELETE FROM requests WHERE id = :requestId")
    suspend fun deleteRequestById(requestId: Uuid)

    @Query("UPDATE requests SET status = :status WHERE id = :requestId")
    suspend fun updateRequestStatus(requestId: Uuid, status: RequestStatusToken)

    // --- UI OBSERVATION (REACTIVE) ---

    /**
     * Observe all active requests for the current user.
     * If the user joins multiple queues, the UI updates automatically.
     */
    @Query("SELECT * FROM requests WHERE userId = :userId ORDER BY localCreationTimestamp DESC")
    fun observeUserRequests(userId: Uuid): Flow<List<Request>>

    /**
     * Observe a specific request. Useful for the "Status Screen" of a single restaurant.
     */
    @Query("SELECT * FROM requests WHERE restaurantId = :restaurantId AND userId = :userId LIMIT 1")
    fun observeRequestForRestaurant(restaurantId: Uuid, userId: Uuid): Flow<Request?>

    // --- ONE-SHOT QUERIES (LOGIC CHECKS) ---

    @Query("SELECT * FROM requests WHERE id = :requestId")
    suspend fun getRequestOnce(requestId: Uuid): Request?

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM requests 
            WHERE userId = :userId 
            AND restaurantId = :restaurantId 
            AND status IN ('QUEUED', 'ACCEPTED')
            LIMIT 1
        )
    """)
    suspend fun containsPendingRestaurant(userId: Uuid, restaurantId: Uuid): Boolean

    @Query("""
        SELECT * FROM requests 
        WHERE restaurantId = :restaurantId 
        AND userId = :userId 
        AND status NOT IN ('CANCELLED', 'PAID') 
        LIMIT 1
    """)
    suspend fun getRequestByRestaurantAndUser(restaurantId: Uuid, userId: Uuid): Request?
}