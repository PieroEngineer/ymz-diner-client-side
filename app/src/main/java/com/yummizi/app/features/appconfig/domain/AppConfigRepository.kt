package com.yummizi.app.features.appconfig.domain

import kotlinx.coroutines.flow.Flow

interface AppConfigRepository {

    /**
     * Evaluates if the minitutorial modal should be shown:
     * Returns false if the user marked "don't show again" OR if already shown today.
     */
    fun shouldShowMinitutorial(): Flow<Boolean>

    suspend fun recordMinitutorialDisplayed()

    suspend fun setMinitutorialCompleted(completed: Boolean)
}