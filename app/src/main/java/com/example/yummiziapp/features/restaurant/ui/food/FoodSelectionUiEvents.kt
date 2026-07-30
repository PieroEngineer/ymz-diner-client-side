package com.example.yummiziapp.features.restaurant.ui.food

import kotlin.uuid.Uuid

interface FoodSelectionUiEvents {
    data class NavigateToPayment(val restaurantId: Uuid) : FoodSelectionUiEvents
}