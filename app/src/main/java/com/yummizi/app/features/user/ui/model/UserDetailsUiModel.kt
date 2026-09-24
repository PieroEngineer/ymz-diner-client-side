package com.yummizi.app.features.user.ui.model

import com.yummizi.app.core.data.local.model.AvailableSpecificsToken

data class UserDetailsUiModel(
    val specifics: List<AvailableSpecificsToken> = emptyList(),
    val companySize: Int = 0,
    val companySizeIsFixed: Boolean = false
    // TODO: Later include the conditions and company here
)
