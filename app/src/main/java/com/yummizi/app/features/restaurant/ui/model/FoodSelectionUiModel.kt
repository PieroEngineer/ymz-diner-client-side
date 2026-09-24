package com.yummizi.app.features.restaurant.ui.model

import kotlin.uuid.Uuid

data class FoodSelectionUiModel(
//X    override val selectionId: Uuid,
    override val dishId: Uuid,
    override val complementaryDishId: Uuid,
    val name: String,
    val amount: Int,
    val unitPriceCents: Int,
    val accumulatedCost: String
) : Identifiable

interface Identifiable {
//X    val selectionId: Uuid
    val dishId: Uuid
    val complementaryDishId: Uuid
}