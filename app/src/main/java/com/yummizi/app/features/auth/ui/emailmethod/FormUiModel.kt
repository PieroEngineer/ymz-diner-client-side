package com.yummizi.app.features.auth.ui.emailmethod

data class FormUiModel(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val acceptTerms: Boolean = false,

    val formIssueText: String? = null
)
