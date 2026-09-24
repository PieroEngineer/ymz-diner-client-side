package com.yummizi.app.features.restaurant.ui.model

import kotlin.uuid.Uuid

data class ComplementaryDishUiModel(
    val dishId: Uuid,
    val dishName: String
)
