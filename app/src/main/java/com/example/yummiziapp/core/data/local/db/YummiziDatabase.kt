package com.example.yummiziapp.core.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.yummiziapp.core.data.local.converters.YummiziConverters
import com.example.yummiziapp.core.data.local.dao.RestaurantDao
import com.example.yummiziapp.core.data.local.dao.LoggedUserDao
import com.example.yummiziapp.core.data.local.dao.RequestDao
import com.example.yummiziapp.core.data.local.dao.MenuDao
import com.example.yummiziapp.core.data.local.dao.PreferenceDao
import com.example.yummiziapp.core.data.local.entities.Dish
import com.example.yummiziapp.core.data.local.entities.LoggedUser
import com.example.yummiziapp.core.data.local.entities.MenuPage
import com.example.yummiziapp.core.data.local.entities.Preference
import com.example.yummiziapp.core.data.local.entities.Request
import com.example.yummiziapp.core.data.local.entities.Restaurant

@Database(
    entities = [
        Restaurant::class,
        LoggedUser::class,
        MenuPage::class,
        Dish::class,
        Request::class,
        Preference::class
    ],
    version = 1,
    exportSchema = false // TODO: Activate it when the app is ready to be released
)
@TypeConverters(YummiziConverters::class)
abstract class YummiziDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
    abstract fun loggedUserDao(): LoggedUserDao
    abstract fun requestDao(): RequestDao
    abstract fun menuDao(): MenuDao
    abstract fun preferenceDao(): PreferenceDao
}