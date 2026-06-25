package com.example.yummiziapp.features.restaurant.ui.model

import com.example.yummiziapp.core.data.local.entities.MenuPage
import kotlin.uuid.Uuid

/// Previously, food was called just at the moment when the user enter to the menu screen or when they are pre-selecting their order
data class MenuUiModel(
    val page: MenuPage, //TODO: Don't give the entity as such, just the necessary attributes
    val coordinatesInPages: List<CoordinatesInPage>, // These contain the coordinates, menu page, etc.
    val dishIds: List<Uuid>
)

data class CoordinatesInPage(   // TODO: Move it to another place
    val x: Float,
    val y: Float
)