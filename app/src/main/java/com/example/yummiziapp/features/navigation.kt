package com.example.yummiziapp.features

import kotlinx.serialization.Serializable

sealed interface YummiziRoute {
    @Serializable data object Home : YummiziRoute
    @Serializable data object Specification : YummiziRoute
    @Serializable data object Preferences : YummiziRoute

    // 🚀 Look here: No strings, no templates! Just pure compile-safe variables.
    @Serializable data class MenuPreview(val restaurantId: String) : YummiziRoute
    @Serializable data class FoodSelection(val restaurantId: String) : YummiziRoute
}