package com.example.yummiziapp.features.restaurant.ui.specification

import com.example.yummiziapp.features.restaurant.ui.model.SelectableUiModel

data class SpecificationUiState(
    val isLoading: Boolean = true,
    val queuedRestaurants: List<SelectableUiModel> = emptyList(),
    val errorMessage: String? = null
)