package com.example.yummiziapp.core.data.di

import com.example.yummiziapp.core.data.repository.AuthRepositoryImpl
import com.example.yummiziapp.core.data.repository.ContactRepositoryImpl
import com.example.yummiziapp.core.data.repository.PaymentRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.yummiziapp.core.data.repository.RestaurantRepositoryImpl
import com.example.yummiziapp.core.data.repository.UserRepositoryImpl
import com.example.yummiziapp.core.data.repository.RequestRepositoryImpl
import com.example.yummiziapp.features.auth.domain.repository.AuthRepository
import com.example.yummiziapp.features.contact.domain.repository.ContactRepository
import com.example.yummiziapp.features.payment.domain.repository.PaymentRepository
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

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl
    ): PaymentRepository

    @Binds
    @Singleton
    abstract fun bindFeedbackRepository(
        contactRepositoryImpl: ContactRepositoryImpl
    ): ContactRepository

    /**
     * Binds the interface to its implementation.
     * Using @Binds is performance-efficient compared to @Provides
     * as Dagger creates no extra factory class instances.
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}