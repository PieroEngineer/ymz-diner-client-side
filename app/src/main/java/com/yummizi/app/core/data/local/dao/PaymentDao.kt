package com.yummizi.app.core.data.local.dao

import androidx.room.Dao

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
