package com.yummizi.app.core.data.repository

import com.yummizi.app.core.data.local.datastore.AppDataSource
import com.yummizi.app.features.appconfig.domain.AppConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppConfigRepositoryImpl @Inject constructor(
    private val appDataSource: AppDataSource
) : AppConfigRepository {

    override fun isMinitutorialCompleted(): Flow<Boolean> {
        return appDataSource.isMinitutorialCompleted
    }

    override suspend fun setMinitutorialCompleted(completed: Boolean) {
        appDataSource.setMinitutorialCompleted(completed)
    }
}