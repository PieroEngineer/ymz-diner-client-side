package com.example.yummiziapp.core.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantDto(
    val id: String,
    val name: String,
    val logoUrl: String,      // Changed from logoId to URL for Cloud
    val sectionType: String,
    val requestId: String = "",
    val dishesToShowUrls: List<String> = emptyList(),
    val menuImageUrls: List<String> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null
)

@Serializable
data class RestaurantMenuDto(
    val menuPages: List<MenuPageDto> = emptyList(),
    val dishes: List<DishDto> = emptyList()
)

@Serializable
data class MenuPageDto(
    val pageId: String,
    val imageUrl: String,
    val pageNumber: Int
)

@Serializable
data class DishDto(
    val id: String,
    val name: String,
    val imageUrl: String,
    val priceCents: Int,
    val prepTime: Int,
    val isOutOfStock: Boolean,
    val menuPageId: String,
    val coordinateXInMenu: Double,
    val coordinateYInMenu: Double,
    val description: String
)