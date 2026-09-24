package com.example.yummiziapp.features.restaurant.domain.model

import kotlin.uuid.Uuid

data class DishIdentifier(
    val dishId: Uuid,
    val name: String
)
