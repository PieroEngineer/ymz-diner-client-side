package com.yummizi.app.features.restaurant.domain.repository

import com.yummizi.app.core.data.local.entities.Dish
import com.yummizi.app.core.data.local.entities.MenuPage
import com.yummizi.app.core.data.local.entities.OrderSelection
import com.yummizi.app.core.data.local.entities.Restaurant
import com.yummizi.app.core.data.local.model.RestaurantWithStatus
import com.yummizi.app.features.restaurant.domain.model.DishIdentifier
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface RestaurantRepository {
    fun observeRestaurantsInUserScope(): Flow<List<Restaurant?>>

    fun observeRestaurantById(id: Uuid): Flow<Restaurant?>

    fun observePendingRestaurantsWithStatus(): Flow<List<RestaurantWithStatus>>

    fun observePendingRestaurantWithStatusById(restaurantId: Uuid): Flow<RestaurantWithStatus?>

    fun observePendingRestaurants(): Flow<List<Restaurant>>

    fun observeOrderSelections(): Flow<List<OrderSelection>>

    suspend fun deleteOrderSelectionById(dishId: Uuid, complementaryDishId: Uuid)

    suspend fun getQuantityOfASelectionById(dishId: Uuid, complementaryDishId: Uuid): Int

    suspend fun updateOrderSelectionQuantity(dishId: Uuid, complementaryDishId: Uuid, delta: Int)

    suspend fun getComplementaryDishesById(dishId: Uuid): List<Uuid>

    suspend fun includeASelection(dishId: Uuid, complementaryDishId: Uuid? = null)

    suspend fun generateNewOrder()

    suspend fun getDishNameById(dishId: Uuid): String
    fun observeRestaurantsByIds(restaurantIds: List<Uuid>): Flow<List<Restaurant>>

    //    suspend fun deleteRestaurant(id: Uuid)
//    fun observeRestaurantMenuWithItsDishes(id: Uuid): Flow<Map<MenuPage, List<Dish>>>

    fun observeRestaurantMenuWithAvailableDishes(id: Uuid): Flow<Map<MenuPage, List<Dish>>>

    fun observeDishes(restaurantId: Uuid): Flow<List<Dish>>

    fun observeNoPendingRestaurantWithStatus(): Flow<RestaurantWithStatus?>

    fun observeMenuPagesByRestaurant(restaurantId: Uuid): Flow<List<MenuPage>>

    suspend fun fetchPaymentInfo(restaurantId: Uuid)

//    suspend fun checkIfThereAreSelections(): Boolean

    suspend fun clearAllRestaurantsAndRequests()

    suspend fun syncFeedRestaurants()

    suspend fun getRestaurantById(id: Uuid): Restaurant?

    suspend fun getAvailableReferencesByDishIds(dishIds: List<Uuid>): List<DishIdentifier>

    /**
     * Synchronizes the remote menu with local storage.
     * High Performance: Uses a single transaction and IO Dispatcher.
     * Elegant: Clean mapping and error handling.
     */
    suspend fun refreshRestaurantMenu(restaurantId: Uuid)

}