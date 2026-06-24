package com.example.yummiziapp.core.data.network.api

import com.example.yummiziapp.core.data.network.model.RestaurantDto
import com.example.yummiziapp.core.data.network.model.RestaurantMenuDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestaurantApi {

    @GET("restaurants/feed")
    suspend fun getFeedRestaurants(
        @Query("userId") userId: String
    ): List<RestaurantDto>

    // 💡 Pro-Tip: Ready for when you need multiple restaurant details by ID later:
    // @GET("restaurants/details")
    // suspend fun getRestaurantsDetails(@Query("ids") restaurantIds: List<String>): List<RestaurantDto>

    @GET("restaurants/{restaurantId}/menu")
    suspend fun getRestaurantMenu(
        @Path("restaurantId") restaurantId: String
    ): RestaurantMenuDto
}