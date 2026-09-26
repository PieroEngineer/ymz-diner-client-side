package com.yummizi.app.features.restaurant.domain.model

import kotlin.uuid.Uuid

data class ComplementaryDishIdsProjection(
    val availableComplementaryDishIds: List<Uuid>
)
