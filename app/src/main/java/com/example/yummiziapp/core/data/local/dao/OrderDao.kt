package com.example.yummiziapp.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.yummiziapp.core.data.local.entities.Order
import com.example.yummiziapp.core.data.local.entities.OrderSelection
import com.example.yummiziapp.core.data.local.model.OrderWithSelectionsRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderSelections(selections: List<OrderSelection>)

    @Transaction
    suspend fun insertOrderWithSelections(orderWithSelections: OrderWithSelectionsRelation) {
        insertOrder(orderWithSelections.order)
        insertOrderSelections(orderWithSelections.selections)
    }

    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    fun observeOrderWithSelections(orderId: String): Flow<OrderWithSelectionsRelation?>

    @Query("SELECT * FROM orders ORDER BY localOrderCreationTimestamp DESC LIMIT 1")
    fun observeLatestOrder(): Flow<Order>

    @Update
    suspend fun updateOrder(order: Order)
}
