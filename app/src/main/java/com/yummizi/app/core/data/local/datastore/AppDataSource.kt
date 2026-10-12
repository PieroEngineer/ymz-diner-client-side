package com.yummizi.app.core.data.local.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private object Keys {
        // ... Unmodified keys ...
        val LAST_MINITUTORIAL_DISPLAYED_TIMESTAMP = longPreferencesKey("last_minitutorial_displayed_timestamp")
        val IS_MINITUTORIAL_COMPLETED = booleanPreferencesKey("is_minitutorial_completed")
    }

    val lastMinitutorialTimestamp: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[Keys.LAST_MINITUTORIAL_DISPLAYED_TIMESTAMP]
    }

    val isMinitutorialCompleted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.IS_MINITUTORIAL_COMPLETED] ?: false
    }

    suspend fun updateLastMinitutorialTimestamp(timestampMillis: Long) {
        dataStore.edit { preferences ->
            preferences[Keys.LAST_MINITUTORIAL_DISPLAYED_TIMESTAMP] = timestampMillis
        }
    }

    suspend fun setMinitutorialCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_MINITUTORIAL_COMPLETED] = completed
        }
    }
}