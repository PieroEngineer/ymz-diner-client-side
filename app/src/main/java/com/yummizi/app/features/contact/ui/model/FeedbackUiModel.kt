package com.yummizi.app.features.contact.ui.model

import com.yummizi.app.core.data.local.model.FeedbackLevelToken

data class FeedbackUiModel(
    val description: String = "",
    val conformityDegree: FeedbackLevelToken? = null
)
