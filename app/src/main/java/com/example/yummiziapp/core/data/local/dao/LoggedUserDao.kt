package com.example.yummiziapp.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.yummiziapp.core.data.local.entities.LoggedUser
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface LoggedUserDao {

    @Upsert
    suspend fun upsertUser(user: LoggedUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: LoggedUser)

    // For the UI: Observe changes in real-time
    @Query("SELECT * FROM logged_user WHERE id = :id")
    fun observeLoggedUser(id: Uuid): Flow<LoggedUser?>

    @Query("SELECT id FROM logged_user LIMIT 1")
    suspend fun getCurrentUserId(): Uuid?

    // For Logic: Get a snapshot right now
    @Query("SELECT * FROM logged_user WHERE id = :id")
    suspend fun getUserSync(id: Uuid?): LoggedUser?

    @Update
    suspend fun updateUser(user: LoggedUser)
}