package com.yummizi.app.core.data.repository

import android.util.Log
import com.yummizi.app.core.data.local.datastore.AppDataSource
import com.yummizi.app.features.appconfig.domain.AppConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

class AppConfigRepositoryImpl @Inject constructor(
    private val appDataSource: AppDataSource
) : AppConfigRepository {

    override fun shouldShowMinitutorial(): Flow<Boolean> {
        return combine(
            appDataSource.isMinitutorialCompleted,
            appDataSource.lastMinitutorialTimestamp
        ) { isCompleted, lastTimestamp ->

            // 1. User selected "Don't show again"
            if (isCompleted) return@combine false

            // 2. Never displayed before
            if (lastTimestamp == null) return@combine true

            // 3. Compare last display date with current device time
            val nowMillis = Clock.System.now().toEpochMilliseconds()
            !isSameDay(lastTimestamp, nowMillis)
        }
    }

    override suspend fun recordMinitutorialDisplayed() {
        val nowMillis = Clock.System.now().toEpochMilliseconds()
        appDataSource.updateLastMinitutorialTimestamp(nowMillis)
    }

    override suspend fun setMinitutorialCompleted(completed: Boolean) {
        appDataSource.setMinitutorialCompleted(completed)
    }

    /**
     * Converts epoch millisecond timestamps into local dates using the device's current timezone
     * to perform an exact calendar day comparison.
     */
    private fun isSameDay(timestamp1Millis: Long, timestamp2Millis: Long): Boolean {
        val systemTimeZone = TimeZone.currentSystemDefault()

        val date1 = Instant.fromEpochMilliseconds(timestamp1Millis)
            .toLocalDateTime(systemTimeZone)
            .date

        val date2 = Instant.fromEpochMilliseconds(timestamp2Millis)
            .toLocalDateTime(systemTimeZone)
            .date

        return date1 == date2
    }


}