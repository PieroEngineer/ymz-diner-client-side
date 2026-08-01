package com.example.yummiziapp.core.data.network.api

import com.example.yummiziapp.core.data.network.model.FeedRestaurantResponseDto
import com.example.yummiziapp.core.data.network.model.RestaurantMenuDto
import com.example.yummiziapp.core.data.network.model.RestaurantPaymentResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestaurantApi {

    @GET("restaurants/feed")
    suspend fun getFeedRestaurants(
        @Query("userId") userId: String
    ): List<FeedRestaurantResponseDto>

    // 💡 Pro-Tip: Ready for when you need multiple restaurant details by ID later:
    // @GET("restaurants/details")
    // suspend fun getRestaurantsDetails(@Query("ids") restaurantIds: List<String>): List<RestaurantDto>

    @GET("restaurants/{restaurantId}/menu")
    suspend fun getRestaurantMenu(
        @Path("restaurantId") restaurantId: String
    ): RestaurantMenuDto

    @GET("restaurants/{restaurantId}/payment")
    suspend fun getPaymentInfo(
        @Path("restaurantId") restaurantId: String
    ): Response<RestaurantPaymentResponseDto>
}