package com.yummizi.app.features.restaurant.ui.food

import kotlin.uuid.Uuid

interface FoodSelectionUiEvents {   // TODO: Change to sealed interface
    data class NavigateToPayment(val restaurantId: Uuid) : FoodSelectionUiEvents
    data object NavigateToHome: FoodSelectionUiEvents

    data class IntentContactRestaurant(val url: String) : FoodSelectionUiEvents
    data class ShowSnackbar(val message: String) : FoodSelectionUiEvents
}