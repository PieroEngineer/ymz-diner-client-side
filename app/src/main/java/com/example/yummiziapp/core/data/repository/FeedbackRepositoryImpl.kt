package com.example.yummiziapp.core.data.repository

//import com.example.yummiziapp.core.data.local.dao.LoggedUserDao
import com.example.yummiziapp.core.data.local.dao.RequestDao
import com.example.yummiziapp.core.data.local.model.FeedbackLevelToken
import com.example.yummiziapp.core.data.network.api.FeedbackApi
import com.example.yummiziapp.core.data.network.model.SendFinalExperienceFeedbackDto
import com.example.yummiziapp.features.feedback.domain.repository.FeedbackRepository
import javax.inject.Inject

class FeedbackRepositoryImpl @Inject constructor(
    private val requestDao: RequestDao,
//    private val loggedUserDao: LoggedUserDao,

    private val yummiziApi: FeedbackApi
) : FeedbackRepository {

    override suspend fun sendFinalExperienceFeedback(conformDegree: FeedbackLevelToken?, description: String) {

        val lastRequest = requestDao.getLatestPaidRequest()

        if ( conformDegree != null || description != ""){
            yummiziApi.sendFinalExperienceFeedback(
                SendFinalExperienceFeedbackDto(
                    userId = "00000000-0000-4000-a000-000000000000",
                    requestId = lastRequest?.requestId.toString(),
                    conformDegree = conformDegree?.name ?: "",
                    description = description
                )
            )
        }

    }
}