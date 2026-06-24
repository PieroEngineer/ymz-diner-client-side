package com.example.yummiziapp.core.data.network.model

import android.util.Log
import com.example.yummiziapp.core.data.local.entities.Dish
import com.example.yummiziapp.core.data.local.entities.MenuPage
import com.example.yummiziapp.core.data.local.entities.Restaurant
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

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

// Mappers for Page
fun MenuPageDto.toEntity(restaurantId: String): MenuPage {  /// Should I create a map folder for these functions?
    return MenuPage(
        id = Uuid.parse(this.pageId), // Handling your DTO's specific naming
        restaurantId = Uuid.parse(restaurantId),
        imageUrl = this.imageUrl,
        pageNumber = this.pageNumber    //TODO: Become it part of the id
    )
}

// Mappers for Dish
fun DishDto.toEntity(): Dish {
    Log.d("MyActivity", "✅🍽️  Before converting to Dish, the dish ${this.name} and ID ${this.id} in the Page ID: $menuPageId")

    return Dish(
        id = Uuid.parse(this.id),
        name = this.name,
        imageUrl = this.imageUrl,
        priceCents = this.priceCents,
        prepTime = this.prepTime,
        isOutOfStock = this.isOutOfStock,
        menuPageId = Uuid.parse(this.menuPageId),
        coordinateXInMenu = this.coordinateXInMenu,
        coordinateYInMenu = this.coordinateYInMenu,
        description = this.description
    )
}

fun RestaurantDto.toEntity() = Restaurant(
    id = id.let { Uuid.parse(it) },// ?: Uuid.NIL,
    name = name,
    logoUrl = logoUrl,
    sectionType = sectionType,
    dishesToShowLink = dishesToShowUrls,
    latitude = latitude,
    longitude = longitude
)