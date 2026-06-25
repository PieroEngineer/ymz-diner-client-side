package com.example.yummiziapp.features.home.ui.model

import com.example.yummiziapp.core.data.local.entities.Restaurant

data class SectionUiModel(
    val id: Int,
    val title: String,
    val restaurants: List<Restaurant>,
    val restaurantBlockHeight: Int
)