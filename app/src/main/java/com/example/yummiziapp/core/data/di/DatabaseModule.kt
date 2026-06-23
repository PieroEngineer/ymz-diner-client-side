package com.example.yummiziapp.core.data.di

import android.content.Context
import androidx.room.Room
import com.yummizi.core.data.local.YummiziDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
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