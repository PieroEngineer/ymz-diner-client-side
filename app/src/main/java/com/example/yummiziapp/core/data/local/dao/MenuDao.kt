package com.example.yummiziapp.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.yummiziapp.core.data.local.entities.Dish
import com.example.yummiziapp.core.data.local.entities.MenuPage
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface MenuDao {
    @Query(
        """
        SELECT * FROM menu_pages 
        JOIN dishes ON menu_pages.id = dishes.menuPageId 
        WHERE menu_pages.restaurantId = :restaurantId
        ORDER BY menu_pages.pageNumber ASC
    """
    )
    fun observeRestaurantMenuWithItsDishes(restaurantId: Uuid): Flow<Map<MenuPage, List<Dish>>>

//    @Query("SELECT * FROM dishes WHERE restaurantId = :restaurantId")
//    fun observeDishesByRestaurant(restaurantId: Uuid): Flow<List<Dish>>

    @Query("SELECT * FROM menu_pages WHERE restaurantId = :restaurantId")
    fun observeMenuPagesByRestaurant(restaurantId: Uuid): Flow<List<MenuPage>>

    @Query(
        """
    SELECT dishes.* FROM dishes 
    INNER JOIN menu_pages ON dishes.menuPageId = menu_pages.id 
    WHERE menu_pages.restaurantId = :restaurantId
    """
    )
    fun observeDishesByRestaurant(restaurantId: Uuid): Flow<List<Dish>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPages(pages: List<MenuPage>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDishes(dishes: List<Dish>)

    @Transaction
    suspend fun clearAndInsertMenu(restaurantId: Uuid, pages: List<MenuPage>, dishes: List<Dish>) {

        deleteDishesForRestaurant(restaurantId)
        deletePagesForRestaurant(restaurantId)

        insertPages(pages)
        insertDishes(dishes)
    }

    @Query("DELETE FROM menu_pages WHERE restaurantId = :restaurantId")
    suspend fun deletePagesForRestaurant(restaurantId: Uuid)

    @Query("DELETE FROM dishes WHERE menuPageId IN (SELECT id FROM menu_pages WHERE restaurantId = :restaurantId)")
    suspend fun deleteDishesForRestaurant(restaurantId: Uuid)

    @Query("SELECT * FROM dishes WHERE id IN (:dishIds)")
    suspend fun getDishesByIds(dishIds: List<Uuid>): List<Dish>

}