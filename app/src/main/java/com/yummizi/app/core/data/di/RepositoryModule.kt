package com.yummizi.app.core.data.di

import com.yummizi.app.core.data.repository.AppConfigRepositoryImpl
import com.yummizi.app.core.data.repository.AuthRepositoryImpl
import com.yummizi.app.core.data.repository.ContactRepositoryImpl
import com.yummizi.app.core.data.repository.PaymentRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.yummizi.app.core.data.repository.RestaurantRepositoryImpl
import com.yummizi.app.core.data.repository.UserRepositoryImpl
import com.yummizi.app.core.data.repository.RequestRepositoryImpl
import com.yummizi.app.features.appconfig.domain.AppConfigRepository
import com.yummizi.app.features.auth.domain.repository.AuthRepository
import com.yummizi.app.features.contact.domain.repository.ContactRepository
import com.yummizi.app.features.payment.domain.repository.PaymentRepository
import com.yummizi.app.features.restaurant.domain.repository.RestaurantRepository
import com.yummizi.app.features.user.domain.repository.UserRepository
import com.yummizi.app.features.request.domain.repository.RequestRepository

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

    @Binds
    @Singleton
    abstract fun bindAppConfigRepository(
        impl: AppConfigRepositoryImpl
    ): AppConfigRepository
}