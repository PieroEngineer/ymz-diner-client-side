package com.example.yummiziapp.features.restaurant.domain.repository

import com.example.yummiziapp.core.data.local.entities.Dish
import com.example.yummiziapp.core.data.local.entities.MenuPage
import com.example.yummiziapp.core.data.local.entities.Restaurant
import com.example.yummiziapp.core.data.local.model.RestaurantWithStatus
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface RestaurantRepository {
    fun observeRestaurantsInUserScope(): Flow<List<Restaurant?>>

    fun observeRestaurantById(id: Uuid): Flow<Restaurant?>

    fun observePendingRestaurantsWithStatus(): Flow<List<RestaurantWithStatus>>

    fun observePendingRestaurantWithStatusById(restaurantId: Uuid): Flow<RestaurantWithStatus?>

    fun observePendingRestaurants(): Flow<List<Restaurant>>

    fun observeRestaurantsByIds(restaurantIds: List<Uuid>): Flow<List<Restaurant>>

    //    suspend fun deleteRestaurant(id: Uuid)
    fun observeRestaurantMenuWithItsDishes(id: Uuid): Flow<Map<MenuPage, List<Dish>>>

    fun observeDishes(restaurantId: Uuid): Flow<List<Dish>>

    fun observeNoPendingRestaurantWithStatus(): Flow<RestaurantWithStatus?>

    fun observeMenuPagesByRestaurant(restaurantId: Uuid): Flow<List<MenuPage>>

    suspend fun fetchPaymentInfo(restaurantId: Uuid)
    suspend fun clearAllRestaurantsAndRequests()

    suspend fun syncFeedRestaurants()

    suspend fun getRestaurantById(id: Uuid): Restaurant?

    /**
     * Synchronizes the remote menu with local storage.
     * High Performance: Uses a single transaction and IO Dispatcher.
     * Elegant: Clean mapping and error handling.
     */
    suspend fun refreshRestaurantMenu(restaurantId: Uuid)

}