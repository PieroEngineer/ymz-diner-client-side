package com.yummizi.app.core.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yummizi.app.core.data.local.converters.YummiziConverters
import com.yummizi.app.core.data.local.dao.RestaurantDao
import com.yummizi.app.core.data.local.dao.LoggedUserDao
import com.yummizi.app.core.data.local.dao.RequestDao
import com.yummizi.app.core.data.local.dao.MenuDao
import com.yummizi.app.core.data.local.dao.OrderDao
import com.yummizi.app.core.data.local.dao.PaymentDao
import com.yummizi.app.core.data.local.dao.PreferenceDao
import com.yummizi.app.core.data.local.entities.Dish
import com.yummizi.app.core.data.local.entities.LoggedUser
import com.yummizi.app.core.data.local.entities.MenuPage
import com.yummizi.app.core.data.local.entities.Order
import com.yummizi.app.core.data.local.entities.OrderSelection
import com.yummizi.app.core.data.local.entities.Preference
import com.yummizi.app.core.data.local.entities.Request
import com.yummizi.app.core.data.local.entities.Restaurant

@Database(
    entities = [
        Restaurant::class,
        LoggedUser::class,
        MenuPage::class,
        Dish::class,
        Request::class,
        Preference::class,
        OrderSelection::class,
        Order::class
    ],
    version = 5,
    exportSchema = false // FIXME: Activate it when the app is ready to be released
)
@TypeConverters(YummiziConverters::class)
abstract class YummiziDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
    abstract fun loggedUserDao(): LoggedUserDao
    abstract fun requestDao(): RequestDao
    abstract fun menuDao(): MenuDao
    abstract fun preferenceDao(): PreferenceDao

    abstract fun paymentDao(): PaymentDao

    abstract fun orderDao(): OrderDao
}