package com.example.yummiziapp.core.data.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.TypeConverters
import com.example.yummiziapp.core.data.local.converters.YummiziConverters
import com.example.yummiziapp.core.data.local.entities.Dish
import com.example.yummiziapp.core.data.local.entities.LoggedUser
import com.example.yummiziapp.core.data.local.entities.MenuPage
import com.example.yummiziapp.core.data.local.entities.Preference
import com.example.yummiziapp.core.data.local.entities.Request
import com.example.yummiziapp.core.data.local.entities.Restaurant
import com.yummizi.core.data.local.YummiziDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Database(
    entities = [
        LoggedUser::class,
        Restaurant::class,
        Request::class,
        MenuPage::class,
        Dish::class,
        Preference::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(YummiziConverters::class)
object DatabaseModule {

    private const val DATABASE_NAME = "yummizi_db"

    @Provides
    @Singleton
    fun provideYummiziDatabase(
        @ApplicationContext context: Context
    ): YummiziDatabase {
        return Room.databaseBuilder(
            context,
            YummiziDatabase::class.java,
            DATABASE_NAME
        )
            // .fallbackToDestructiveMigration() // Use ONLY during initial dev; remove before production release!
            .build()
    }
}