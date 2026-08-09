package com.example.yummiziapp.core.data.repository

//import com.example.yummiziapp.core.data.local.dao.LoggedUserDao
import android.util.Log
import com.example.yummiziapp.core.data.local.dao.RequestDao
import com.example.yummiziapp.core.data.local.model.FeedbackLevelToken
import com.example.yummiziapp.core.data.network.api.ContactApi
import com.example.yummiziapp.core.data.network.model.SendFinalExperienceFeedbackDto
import com.example.yummiziapp.features.contact.domain.repository.ContactRepository
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val requestDao: RequestDao,
//    private val loggedUserDao: LoggedUserDao,

    private val yummiziApi: ContactApi
) : ContactRepository {

    override suspend fun sendFeedback(conformDegree: FeedbackLevelToken?, description: String) {

        val lastRequest = requestDao.getLatestPaidRequest()

        val associatedRequestId = lastRequest?.requestId?.toString() ?: ""  // it determines if the feedback is related to a request or not

        try{
            if ( conformDegree != null || description != ""){
                yummiziApi.sendFeedback(
                    SendFinalExperienceFeedbackDto(
                        userId = "00000000-0000-4000-a000-000000000000",
                        requestId = associatedRequestId,
                        conformDegree = conformDegree?.name ?: "",
                        description = description
                    )
                )
            }
        } catch (e: Exception){
            Log.e("Yummizi", "🚩☁️ The feedback couldn't be send remotly", e)
            // TODO: Save the feedback locally so later when connection is back, the app automatically send it.
        }


    }
}