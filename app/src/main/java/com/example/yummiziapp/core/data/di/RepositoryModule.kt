package com.example.yummiziapp.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.yummiziapp.core.data.repository.RestaurantRepositoryImpl
import com.example.yummiziapp.core.data.repository.UserRepositoryImpl
import com.example.yummiziapp.core.data.repository.RequestRepositoryImpl
import com.example.yummiziapp.features.restaurant.domain.repository.RestaurantRepository
import com.example.yummiziapp.features.user.domain.repository.UserRepository
import com.example.yummiziapp.features.request.domain.repository.RequestRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRestaurantRepository(
        restaurantRepositoryImpl: RestaurantRepositoryImpl
    ): RestaurantRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindRequestRepository(
        requestRepositoryImpl: RequestRepositoryImpl
    ): RequestRepository
}