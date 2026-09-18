package com.example.yummiziapp.core.data.repository

import android.util.Log
import com.amplifyframework.kotlin.core.Amplify
import com.example.yummiziapp.core.data.di.IoDispatcher
import com.example.yummiziapp.core.data.local.dao.LoggedUserDao
import com.example.yummiziapp.core.data.local.dao.PreferenceDao
import com.example.yummiziapp.core.data.local.entities.LoggedUser
import com.example.yummiziapp.core.data.local.entities.Preference
import com.example.yummiziapp.core.data.network.api.UserApi
import com.example.yummiziapp.core.data.network.model.UserCreationPayload
import com.example.yummiziapp.core.data.network.model.toDto
import com.example.yummiziapp.features.user.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.uuid.Uuid


class UserRepositoryImpl @Inject constructor(
    private val api: UserApi,
    private val loggedUserDao: LoggedUserDao,
    private val preferenceDao: PreferenceDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): UserRepository {

    override fun observePreferences() = preferenceDao.observePreferences(Uuid.parse("00000000-0000-4000-a000-000000000000")) // TODO: Remove this hardcoded value when daling with preferences

    // We observe the logged user. If the lists change, the flow emits.
    override fun observeCurrentUserInfo(): Flow<LoggedUser?> {
        return loggedUserDao.observeLoggedUser()
    }

    override suspend fun savePreference(preference: Preference): Result<Unit> = withContext(ioDispatcher) { //TODO: Normilize the usage of "Result" in the rest of the app where necessary

        val userId = loggedUserDao.getCurrentUserId() ?: return@withContext Result.failure(IllegalStateException("No logged user found"))

        // Local Upsert (Immediate)
        preferenceDao.upsertPreference(preference)

        // Network Sync
        return@withContext try {
            val response = api.savePreference(userId.toString(), preference.id.toString(), preference.toDto())

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MenuRepository", "❌ Fail to save preference remotely", e)
            preferenceDao.deletePreference(preference.id)

            Result.failure(e)
        }
    }

    override suspend fun deletePreference(id: Uuid)= withContext(ioDispatcher) {
        // We need the current userId for the Partition Key
        val userId = loggedUserDao.getCurrentUserId() ?: return@withContext

        val previousPreference = preferenceDao.getPreferenceById(id)?.copy() ?: return@withContext

        // Local Delete
        preferenceDao.deletePreference(id)

        // Remote Delete (Providing both keys)
        try {
            api.deletePreference(
                userId = userId.toString(),
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

    override suspend fun updateCompanySize(isIncreasing: Boolean) = withContext(ioDispatcher) {
        if (isIncreasing) {
            loggedUserDao.increaseCompanySize()
        } else {
            loggedUserDao.decreaseCompanySize()
        }
    }

    override suspend fun createNewUser(username: String) {
        val userId = Amplify.Auth.getCurrentUser().userId
        api.createUser(
            userId = userId,
            user = UserCreationPayload(
                username = username
            )
        )
    }
}
