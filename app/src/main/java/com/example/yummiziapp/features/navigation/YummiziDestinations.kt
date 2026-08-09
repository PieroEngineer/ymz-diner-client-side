package com.example.yummiziapp.features.navigation

import kotlinx.serialization.Serializable

sealed interface YummiziRoute {
    @Serializable data object Home : YummiziRoute
    @Serializable data class Specification(val restaurantId: String?) : YummiziRoute
    @Serializable data object Preferences : YummiziRoute
    @Serializable data object Contact : YummiziRoute

    // 🚀 Look here: No strings, no templates! Just pure compile-safe variables.
    @Serializable data class MenuPreview(val restaurantId: String) : YummiziRoute
    @Serializable data class FoodSelection(val restaurantId: String) : YummiziRoute

    @Serializable data class PaymentPreparation(val restaurantId: String) : YummiziRoute
    @Serializable data class Arrival(val restaurantId: String) : YummiziRoute

}