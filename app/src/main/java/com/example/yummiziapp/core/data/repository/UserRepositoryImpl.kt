package com.example.yummiziapp.core.data.repository

import android.util.Log
import com.example.yummiziapp.core.data.di.IoDispatcher
import com.example.yummiziapp.core.data.local.dao.LoggedUserDao
import com.example.yummiziapp.core.data.local.dao.PreferenceDao
import com.example.yummiziapp.core.data.local.entities.LoggedUser
import com.example.yummiziapp.core.data.local.entities.Preference
import com.example.yummiziapp.core.data.network.api.PreferenceApi
import com.example.yummiziapp.core.data.network.model.toDto
import com.example.yummiziapp.features.auth.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.uuid.Uuid


class UserRepositoryImpl @Inject constructor(
    private val api: PreferenceApi,
    private val loggedUserDao: LoggedUserDao,
    private val preferenceDao: PreferenceDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): UserRepository {

    override fun observePreferences() = preferenceDao.observePreferences(Uuid.parse("00000000-0000-4000-a000-000000000000"))


    override suspend fun seedMockUser() = withContext(ioDispatcher) {
        val mockUuid = Uuid.parse("00000000-0000-4000-a000-000000000000")

        // If the user is already in the table, skip upsert
        if (mockUuid != loggedUserDao.getCurrentUserId()) {
            val mockUser = LoggedUser(
                id = mockUuid,
                name = "Sir Mock User",
                favoriteRestaurants = emptyList()
            )

            Log.d("MyActivity", "👤 A user mockup was built\n$mockUser")

            // We use a specific Dao method to save this
            loggedUserDao.upsertUser(mockUser)
            Log.d("MyActivity", "✅👤 A user mockup was saved in room")
        }
        else{
            Log.d("MyActivity", "✅👤 A user mockup was already built")
        }

    }

    // We observe the logged user. If the lists change, the flow emits.
    override fun observeCurrentUserInfo(): Flow<LoggedUser?> {
        return loggedUserDao.observeLoggedUser(Uuid.parse("00000000-0000-4000-a000-000000000000"))  /// Mock-up movement, change it later
    }

    override suspend fun savePreference(preference: Preference): Result<Unit> = withContext(ioDispatcher) { //TODO: Normilize the usage of "Result" in the rest of the app

        val userId = "00000000-0000-4000-a000-000000000000"

        // Local Upsert (Immediate)
        preferenceDao.upsertPreference(preference)

        // Network Sync
        return@withContext try {
            val response = api.savePreference(userId, preference.id.toString(), preference.toDto())

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MenuRepository", "❌ Fail to save preference remotely", e)
            preferenceDao.deletePreference(preference.id)

            Result.failure(e)
        }
    }

    override suspend fun deletePreference(id: Uuid)= withContext(ioDispatcher) {
        // We need the current userId for the Partition Key
        val userId = "00000000-0000-4000-a000-000000000000" //Mockup user ID for now ///sessionManager.getUserId() ?: return

        val previousPreference = preferenceDao.getPreferenceById(id)?.copy() ?: return@withContext

        // Local Delete
        preferenceDao.deletePreference(id)

        // Remote Delete (Providing both keys)
        try {
            api.deletePreference(
                userId = userId,
                prefId = id.toString()
            )

            Unit
        } catch (e: Exception) {
            // TODO: Show a Toast/SnackBar
            Log.e("MyActivity", "❌ Fail to delete preference", e)
            preferenceDao.upsertPreference(previousPreference)  // Rollback //TODO: Consider soft-delete (I saved its concept in NotebookLM)

            Unit
        }
    }

}
