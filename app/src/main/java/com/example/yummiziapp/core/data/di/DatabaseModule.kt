package com.example.yummiziapp.core.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.yummiziapp.core.data.local.dao.RestaurantDao
import com.example.yummiziapp.core.data.local.dao.LoggedUserDao
import com.example.yummiziapp.core.data.local.dao.RequestDao
import com.example.yummiziapp.core.data.local.dao.MenuDao
import com.example.yummiziapp.core.data.local.dao.OrderDao
import com.example.yummiziapp.core.data.local.dao.PaymentDao
import com.example.yummiziapp.core.data.local.dao.PreferenceDao
import com.example.yummiziapp.core.data.local.db.YummiziDatabase

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): YummiziDatabase {
        return Room.databaseBuilder(
            context,
            YummiziDatabase::class.java,
            "yummizi_db"
        )
//            .fallbackToDestructiveMigration(true)   // TODO: Remove in production
            .build()
    }

    @Provides
    fun provideRestaurantDao(db: YummiziDatabase): RestaurantDao = db.restaurantDao()

    @Provides
    fun provideLoggedUserDao(db: YummiziDatabase): LoggedUserDao = db.loggedUserDao()

    @Provides
    fun provideRequestDao(db: YummiziDatabase): RequestDao = db.requestDao()

    @Provides
    fun provideMenuDao(db: YummiziDatabase): MenuDao = db.menuDao()

    @Provides
    fun providePreferenceDao(db: YummiziDatabase): PreferenceDao = db.preferenceDao()

    @Provides
    fun providePaymentDao(db: YummiziDatabase): PaymentDao = db.paymentDao()

    @Provides
    fun provideOrderDao(db: YummiziDatabase): OrderDao = db.orderDao()
}