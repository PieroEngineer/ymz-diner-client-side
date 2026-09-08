package com.example.yummiziapp.features.user.domain.repository

import com.example.yummiziapp.core.data.local.entities.LoggedUser
import com.example.yummiziapp.core.data.local.entities.Preference
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface UserRepository {

    /**
     * Reactive stream of the currently logged-in user.
     * Collect this in ViewModels to update the UI instantly when lists change.
     */
    fun observeCurrentUserInfo(): Flow<LoggedUser?>

    /**
     * Mock method for development to seed the local database with a test user.
     * HINT: Replace this later with a real login flow.
     */
//X    suspend fun seedUser()

    suspend fun savePreference(preference: Preference): Result<Unit>

    suspend fun deletePreference(id: Uuid)

    fun observePreferences(): Flow<List<Preference>>

    suspend fun updateCompanySize(isIncreasing: Boolean)
}