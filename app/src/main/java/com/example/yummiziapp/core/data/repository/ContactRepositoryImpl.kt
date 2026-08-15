package com.example.yummiziapp.core.data.repository

//import com.example.yummiziapp.core.data.local.dao.LoggedUserDao
import android.util.Log
import com.example.yummiziapp.core.data.di.IoDispatcher
import com.example.yummiziapp.core.data.local.dao.LoggedUserDao
import com.example.yummiziapp.core.data.local.dao.RequestDao
import com.example.yummiziapp.core.data.local.model.FeedbackLevelToken
import com.example.yummiziapp.core.data.network.api.ContactApi
import com.example.yummiziapp.core.data.network.model.SendFeedbackDto
import com.example.yummiziapp.core.data.network.model.SendRestaurantInfoDto
import com.example.yummiziapp.features.contact.domain.repository.ContactRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val requestDao: RequestDao,
    private val loggedUserDao: LoggedUserDao,

    private val yummiziApi: ContactApi,

    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ContactRepository {

    override suspend fun sendFeedback(conformDegree: FeedbackLevelToken?, description: String) {
        withContext(ioDispatcher) {

            val userId = loggedUserDao.getCurrentUserId() ?: return@withContext // TODO: Rethink the consequent action

            val lastRequest = requestDao.getLatestPaidRequest() // FIXME: Change it by a function that get a READY or a PAID request

            val associatedRequestId = lastRequest?.requestId?.toString()
                ?: ""  // it determines if the feedback is related to a request or not

            try {
                if (conformDegree != null || description != "") {
                    yummiziApi.sendFeedback(
                        SendFeedbackDto(
                            userId = userId.toString(),
                            requestId = associatedRequestId,
                            conformDegree = conformDegree?.name ?: "",
                            description = description
                        )
                    )
                }

            } catch (e: Exception) {
                Log.e("Yummizi", "🚩☁️ The feedback couldn't be send remotly", e)
                // TODO (#11): Save the feedback locally so later when connection is back, the app automatically send it.
            }
        }
    }

    override suspend fun sendRestaurantInfo( restaurantName: String, restaurantContact: String, restaurantComment: String ) {
        withContext(ioDispatcher) {

            val userId = loggedUserDao.getCurrentUserId() ?: return@withContext // TODO: Rethink the consequent action

            try {

                val allBlank = listOf(
                    restaurantName,
                    restaurantContact,
                    restaurantComment
                ).all { it.isBlank() }
                if (allBlank) return@withContext

                yummiziApi.sendRestaurantInfo(
                    SendRestaurantInfoDto(
                        userId = userId.toString(),
                        restaurantName = restaurantName,
                        restaurantContact = restaurantContact,
                        restaurantComment = restaurantComment
                    )
                )

            } catch (e: Exception) {
                Log.e("Yummizi", "🚩☁️ The restaurant info couldn't be send remotly", e)
                // TODO (#11)
            }

        }
    }
}