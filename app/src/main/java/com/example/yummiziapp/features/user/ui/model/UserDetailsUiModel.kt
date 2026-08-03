package com.example.yummiziapp.features.user.ui.model

import com.example.yummiziapp.core.data.local.model.AvailableSpecificsToken

data class UserDetailsUiModel(
    val specifics: List<AvailableSpecificsToken>,
    val companySize: Int
    // TODO: Later include the conditions and company here
)
