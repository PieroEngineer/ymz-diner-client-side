package com.example.yummiziapp.features.feedback.domain.repository

import com.example.yummiziapp.core.data.local.model.FeedbackLevelToken

interface FeedbackRepository {

    suspend fun sendFinalExperienceFeedback(conformDegree: FeedbackLevelToken?, description: String)
}