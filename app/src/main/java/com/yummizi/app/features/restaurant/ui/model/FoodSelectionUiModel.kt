package com.yummizi.app.features.restaurant.ui.model

import kotlin.uuid.Uuid

data class FoodSelectionUiModel(
    override val id: Uuid,
    val name: String,
    val amount: Int,
    val unitPriceCents: Int,
    val accumulatedCost: String
) : Identifiable

interface Identifiable {
    val id: Uuid
}