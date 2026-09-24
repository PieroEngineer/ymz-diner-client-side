package com.yummizi.app.features.request.data.di

// TODO: Every feature should have their own repository module like this.
//import com.yummizi.app.RestaurantRepositoryImpl
//import com.yummizi.app.RestaurantRepository
//import dagger.Binds
//import dagger.Module
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import jakarta.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//abstract class RequestFeatureModule {
//
//    @Binds
//    @Singleton
//    abstract fun bindRestaurantRepository(
//        impl: RestaurantRepositoryImpl
//    ): RestaurantRepository
//}