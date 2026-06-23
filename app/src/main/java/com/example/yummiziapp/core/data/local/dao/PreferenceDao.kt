package com.example.yummiziapp.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.yummiziapp.core.data.local.entities.Preference
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface PreferenceDao {
    @Upsert
    suspend fun upsertPreference(preference: Preference)

    @Query("SELECT * FROM preferences WHERE userId = :userId ORDER BY createdAt DESC")
    fun observePreferences(userId: Uuid): Flow<List<Preference>>

    @Query("SELECT * FROM preferences WHERE id = :preferenceId")    //TODO: Avoid "Select *": Use Projections to fetch only the specific columns needed for a particular screen. This reduces memory overhead and improves IO speed.
    suspend fun getPreferenceById(preferenceId: Uuid?): Preference?

    @Query("DELETE FROM preferences WHERE id = :id")
    suspend fun deletePreference(id: Uuid)
}