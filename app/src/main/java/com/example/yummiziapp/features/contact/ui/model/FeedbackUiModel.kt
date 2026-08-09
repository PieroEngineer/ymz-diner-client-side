package com.example.yummiziapp.features.contact.ui.model

import com.example.yummiziapp.core.data.local.model.FeedbackLevelToken

data class FeedbackUiModel(
    val description: String = "",
    val conformityDegree: FeedbackLevelToken? = null
)
