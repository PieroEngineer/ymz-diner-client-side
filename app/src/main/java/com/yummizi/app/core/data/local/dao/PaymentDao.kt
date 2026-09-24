package com.yummizi.app.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yummizi.app.core.data.local.entities.OrderSelection
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderSelections(selections: List<OrderSelection>)

    @Query("DELETE FROM order_selections")
    suspend fun deleteAllOrderSelections()

    @Query("SELECT * FROM order_selections")
    fun observeOrderSelections(): Flow<List<OrderSelection>>

    @Query("SELECT * FROM order_selections")
    suspend fun getOrderSelections(): List<OrderSelection>
}
