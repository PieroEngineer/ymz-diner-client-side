package com.yummizi.app.features.appconfig.domain

import kotlinx.coroutines.flow.Flow

interface AppConfigRepository {

    fun isMinitutorialCompleted(): Flow<Boolean>

    suspend fun setMinitutorialCompleted(completed: Boolean)
}