package com.example.yummiziapp.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yummiziapp.core.data.local.entities.OrderSelection
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao { // TODO: Move all the current active functions to the order DAO

    //X
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertOrderSelections(selections: List<OrderSelection>)

    //X
//    @Query("DELETE FROM order_selections")
//    suspend fun deleteAllOrderSelections()

    //X
//    @Query("SELECT * FROM order_selections")
//    fun observeOrderSelections(): Flow<List<OrderSelection>>


}
