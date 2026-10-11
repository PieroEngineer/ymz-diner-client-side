package com.yummizi.app.core.data.local.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private object PreferencesKeys {
        val IS_MINITUTORIAL_COMPLETED = booleanPreferencesKey("is_minitutorial_completed")
//        val IS_DARK_MODE_ENABLED = booleanPreferencesKey("is_dark_mode_enabled")
    }

    val isMinitutorialCompleted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_MINITUTORIAL_COMPLETED] ?: false
    }

    suspend fun setMinitutorialCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_MINITUTORIAL_COMPLETED] = completed
        }
    }
}