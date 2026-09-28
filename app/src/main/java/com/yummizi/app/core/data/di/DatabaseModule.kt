package com.yummizi.app.core.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.yummizi.app.core.data.local.dao.RestaurantDao
import com.yummizi.app.core.data.local.dao.LoggedUserDao
import com.yummizi.app.core.data.local.dao.RequestDao
import com.yummizi.app.core.data.local.dao.MenuDao
import com.yummizi.app.core.data.local.dao.OrderDao
import com.yummizi.app.core.data.local.dao.PaymentDao
import com.yummizi.app.core.data.local.dao.PreferenceDao
import com.yummizi.app.core.data.local.db.YummiziDatabase

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
//            .addMigrations(MIGRATION_2_3) // For manual migrations
//            .fallbackToDestructiveMigration(true) // For testing
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