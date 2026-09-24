package com.yummizi.app.features.home.ui.model

import com.yummizi.app.core.data.local.entities.Restaurant

data class SectionUiModel(
    val id: Int,
    val title: String,
    val restaurants: List<Restaurant>,
    val restaurantBlockHeight: Int
)