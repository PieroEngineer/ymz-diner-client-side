package com.yummizi.app.features.user.ui.model

import kotlin.uuid.Uuid

data class RestaurantIdentityUiModel(
    val restaurantId: Uuid? = null,
    val imageUrl: String? = null,
    val name: String = ""
)