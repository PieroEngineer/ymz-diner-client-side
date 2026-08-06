package com.example.yummiziapp.features.restaurant.ui.model

import kotlin.uuid.Uuid

data class RestaurantIdentityUiModel(
    val restaurantId: Uuid? = null,
    val imageUrl: String? = null,
    val name: String = ""
)
