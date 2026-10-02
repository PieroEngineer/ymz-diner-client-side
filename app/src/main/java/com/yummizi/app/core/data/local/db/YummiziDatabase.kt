package com.yummizi.app.core.data.local.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
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
    // version 5 will be treated like the first version
//    version = 5,  /// Last modification: 27-Sep 2026 at 2pm
//    version = 6,  /// Last modification: 01-Oct 2026 at 8:30pm
    version = 7,  /// Last modification: 02-Oct 2026 at 4:00pm
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 5, to = 6), // Room auto-detects the new nullable column
        AutoMigration(
            from = 6,
            to = 7,
            spec = YummiziDatabase.RequestColumnRename6To7Spec::class
        )
    ]
)
@TypeConverters(YummiziConverters::class)
abstract class YummiziDatabase : RoomDatabase() {

    // Explicitly mapping renamed columns to prevent Room from dropping and recreating them
    @RenameColumn(
        tableName = "requests",
        fromColumnName = "localCreationTimestamp",
        toColumnName = "onQueuedTimestamp"
    )
    @RenameColumn(
        tableName = "requests",
        fromColumnName = "localConfirmationTimestamp",
        toColumnName = "onContinueAcceptanceLocalTimestamp"
    )
    class RequestColumnRename6To7Spec : AutoMigrationSpec

    abstract fun restaurantDao(): RestaurantDao
    abstract fun loggedUserDao(): LoggedUserDao
    abstract fun requestDao(): RequestDao
    abstract fun menuDao(): MenuDao
    abstract fun preferenceDao(): PreferenceDao

    abstract fun paymentDao(): PaymentDao

    abstract fun orderDao(): OrderDao
}