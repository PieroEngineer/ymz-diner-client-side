package com.example.yummiziapp.features.contact.domain.repository

import com.example.yummiziapp.core.data.local.model.FeedbackLevelToken

interface ContactRepository {

    suspend fun sendFeedback(conformDegree: FeedbackLevelToken?, description: String)

    suspend fun sendRestaurantInfo( restaurantName: String, restaurantContact: String, restaurantComment: String )
}