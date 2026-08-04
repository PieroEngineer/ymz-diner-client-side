package com.example.yummiziapp.features.payment.ui.model

import com.example.yummiziapp.core.data.local.model.FeedbackLevelToken

data class FeedbackUiState(
    val description: String = "",
    val conformityDegree: FeedbackLevelToken? = null
)
