package com.example.yummiziapp.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yummiziapp.core.data.local.entities.Restaurant
import com.example.yummiziapp.core.data.local.model.RequestStatusToken
import com.example.yummiziapp.core.data.local.model.RestaurantWithStatus
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface RestaurantDao {   // TODO: Remove the Uuid of their arguments because there is only one user

    @Query("DELETE FROM restaurants")
    suspend fun clearRestaurantsFromFeed()

    @Query("SELECT * FROM restaurants")
    fun getFeedRestaurants(): Flow<List<Restaurant>>    /// *1 I think it shouldn't be a flow because the section will be updated everytime but the feed should be static once is called to don't confuse the user

    @Query("SELECT * FROM restaurants WHERE id = :restaurantId")
    fun observeRestaurantById(restaurantId: Uuid): Flow<Restaurant?>

    // Reactive: Updates whenever any restaurant in the list changes
    @Query("SELECT * FROM restaurants WHERE id IN (:restaurantIds)")
    fun observeRestaurantsByIds(restaurantIds: List<Uuid>): Flow<List<Restaurant>>

    @Query("""
        SELECT restaurants.*, requests.status as requestStatus 
        FROM restaurants 
        JOIN requests ON restaurants.id = requests.restaurantId 
        WHERE requests.userId = (SELECT id FROM logged_user LIMIT 1) 
        AND requests.status IN (:pendingStatuses)
        ORDER BY requests.localCreationTimestamp ASC
    """)
    fun observePendingRestaurantsWithStatus(
        pendingStatuses: List<RequestStatusToken> = listOf(RequestStatusToken.QUEUED, RequestStatusToken.ACCEPTED)
    ): Flow<List<RestaurantWithStatus>>

    @Query("""
        SELECT 
            restaurants.*, 
            COALESCE(requests.status, 'IDLE') as requestStatus 
        FROM restaurants 
        LEFT JOIN requests ON restaurants.id = requests.restaurantId 
            AND requests.userId = (SELECT id FROM logged_user LIMIT 1)
        WHERE restaurants.id = :restaurantId
        AND (requests.status IS NULL OR requests.status IN (:pendingStatuses))
    """)
    fun observePendingRestaurantWithStatusById(
        restaurantId: Uuid,
        pendingStatuses: List<RequestStatusToken> = listOf(RequestStatusToken.QUEUED, RequestStatusToken.ACCEPTED)
    ): Flow<RestaurantWithStatus?>

    @Query("""
        SELECT 
            restaurants.*, 
            requests.status as requestStatus 
        FROM restaurants 
        JOIN requests ON restaurants.id = requests.restaurantId
        WHERE requests.userId = (SELECT id FROM logged_user LIMIT 1)
        AND requests.status IN (:noPendingStatuses)
        LIMIT 1
    """)
    fun observeNoPendingRestaurantWithStatus(
        noPendingStatuses: List<RequestStatusToken> = listOf(RequestStatusToken.PAID, RequestStatusToken.PAY_TRIED, RequestStatusToken.READY) // TODO: Consider to create a some kind of global value containing the lists of pending and not pending requests status
    ): Flow<RestaurantWithStatus?>

    /**
     * Even though we JOIN with the requests table to filter the results,
     * we only SELECT restaurants.*, so Room maps it directly to a List<Restaurant>.
     */
    @Query("""
        SELECT restaurants.* FROM restaurants 
        JOIN requests ON restaurants.id = requests.restaurantId 
        WHERE requests.userId = (SELECT id FROM logged_user LIMIT 1) 
        AND requests.status IN ('QUEUED', 'ACCEPTED')
        ORDER BY requests.localCreationTimestamp ASC
    """)
    fun observePendingRestaurants(): Flow<List<Restaurant>>

//    @Query("""
//        SELECT restaurants.* FROM restaurants
//        JOIN requests ON restaurants.id = requests.restaurantId
//        WHERE requests.userId = (SELECT id FROM logged_user LIMIT 1)
//        AND requests.status = 'READY'
//        ORDER BY requests.localCreationTimestamp DESC
//        LIMIT 1
//    """)
//    fun observeReadyRestaurant(): Flow<Restaurant?>

    @Query("SELECT * FROM restaurants WHERE id = :restaurantId")    //TODO: Avoid "Select *": Use Projections to fetch only the specific columns needed for a particular screen. This reduces memory overhead and improves IO speed.
    suspend fun getRestaurantById(restaurantId: Uuid?): Restaurant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurants(restaurants: List<Restaurant>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurant(restaurant: Restaurant)

    @Query("DELETE FROM restaurants WHERE id = :restaurantId")
    suspend fun deleteById(restaurantId: Uuid)

    @Query("""
        UPDATE restaurants 
        SET phoneNumberToPay = :phoneToPay, 
            phoneNumberToContact = :phoneToContact, 
            paymentQrUrl = :qrUrl 
        WHERE id = :restaurantId
    """)
    suspend fun updatePaymentInfo(
        restaurantId: Uuid,
        phoneToPay: String,
        phoneToContact: String,
        qrUrl: String
    )

    @Query("DELETE FROM restaurants")
    suspend fun clearAll()

//    @Query("UPDATE restaurants SET requestId = :requestId WHERE id = :restaurantId")
//    suspend fun assignRequestId(restaurantId: Uuid, requestId: String?)
}