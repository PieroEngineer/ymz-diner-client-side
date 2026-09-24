package com.example.yummiziapp.features.restaurant.ui.model

import kotlin.uuid.Uuid

data class ComplementSelectionUiState(
    val mainDishId: Uuid? = null,
    val availableComplementaryDishes: List<ComplementaryDishUiModel> = emptyList(),

    val mainDishName: String? = null,

    val isModalOpen: Boolean = false,
)
