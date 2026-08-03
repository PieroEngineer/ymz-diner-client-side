package com.example.yummiziapp.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yummiziapp.core.data.local.entities.OrderSelection
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderSelections(selections: List<OrderSelection>) //cp: Include the order ID

    @Query("DELETE FROM order_selections")
    suspend fun deleteAllOrderSelections()

    @Query("SELECT * FROM order_selections")
    fun observeOrderSelections(): Flow<List<OrderSelection>>

    @Query("SELECT * FROM order_selections")
    suspend fun getOrderSelections(): List<OrderSelection>
}
