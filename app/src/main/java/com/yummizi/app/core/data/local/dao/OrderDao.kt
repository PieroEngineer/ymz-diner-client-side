package com.yummizi.app.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.yummizi.app.core.data.local.entities.Order
import com.yummizi.app.core.data.local.entities.OrderSelection
import com.yummizi.app.core.data.local.model.OrderWithSelectionsRelation
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createOrderBase(order: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderSelections(selections: List<OrderSelection>)

    //X
//    @Transaction
//    suspend fun insertOrderWithSelections(orderWithSelections: OrderWithSelectionsRelation) {
//        createOrder(orderWithSelections.order)
//        insertOrderSelections(orderWithSelections.selections)
//    }

//    @Query("SELECT * FROM orders ORDER BY localOrderCreationTimestamp DESC LIMIT 1")
//    suspend fun getLatestOrder(): Order

    @Query("SELECT orderId FROM orders ORDER BY localOrderCreationTimestamp DESC LIMIT 1")  // TODO: Remove the "ORDER BY localOrderCreationTimestamp" because it's not necessary due there is only one order at the time
    suspend fun getCurrentOrderId(): String

//    @Transaction
//    @Query("SELECT * FROM orders WHERE orderId = :orderId")
//    fun observeOrderWithSelections(orderId: String): Flow<OrderWithSelectionsRelation?>

    @Query("SELECT * FROM orders ORDER BY localOrderCreationTimestamp DESC LIMIT 1")
    fun observeLatestOrder(): Flow<Order>

    @Query("SELECT * FROM order_selections")
    fun observeOrderSelections():Flow<List<OrderSelection>>

    @Query("SELECT * FROM order_selections")
    suspend fun getCurrentOrderSelections(): List<OrderSelection> /// Will I need to specify the order ID?

    @Query("SELECT * FROM order_selections")
    suspend fun getOrderSelections(): List<OrderSelection>

    @Query("SELECT EXISTS (SELECT 1 FROM order_selections)")
    suspend fun checkIfThereAreSelections(): Boolean

    @Query("DELETE FROM order_selections WHERE dishId = :dishId AND complementaryDishId = :complementaryDishId")
    suspend fun deleteOrderSelectionById(dishId: Uuid, complementaryDishId: Uuid)

    @Query("SELECT quantity FROM order_selections WHERE dishId = :dishId AND complementaryDishId = :complementaryDishId")
    suspend fun getQuantityOfASelectionById(dishId: Uuid, complementaryDishId: Uuid): Int

    @Query("UPDATE order_selections SET quantity = quantity + :delta WHERE dishId = :dishId AND complementaryDishId = :complementaryDishId")
    suspend fun updateSelectionQuantityById(dishId: Uuid, complementaryDishId: Uuid, delta: Int)

    @Query("SELECT EXISTS (SELECT 1 FROM order_selections WHERE dishId = :dishId AND complementaryDishId = :complementaryDishId)")
    suspend fun checkIfSelectionExists(dishId: Uuid, complementaryDishId: Uuid): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewSelection(newOrderSelection: OrderSelection)

    @Update
    suspend fun updateOrder(order: Order)
}
