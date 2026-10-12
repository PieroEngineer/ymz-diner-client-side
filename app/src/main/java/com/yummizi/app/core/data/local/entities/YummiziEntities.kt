package com.yummizi.app.core.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.yummizi.app.core.data.local.model.AvailableSpecificsToken
import com.yummizi.app.core.data.local.model.DishTypeToken
import com.yummizi.app.core.data.local.model.RequestStatusToken
//X import com.yummizi.app.features.request.data.model.QueueStatus
import kotlin.uuid.Uuid
//import kotlinx.serialization.Serializable
import kotlin.time.Instant


@Entity(tableName = "logged_user")
data class LoggedUser(
    @PrimaryKey
    @ColumnInfo(name ="id")
    val id: Uuid, // The "userId"

    @ColumnInfo(name ="name")
    val name: String,

    @ColumnInfo(name ="favoriteRestaurants")
    val favoriteRestaurants: List<Uuid> = emptyList(),

    @ColumnInfo(name ="companySize")
    val companySize: Int,

    @ColumnInfo(name ="specifics")
    val specifics: List<AvailableSpecificsToken>,
)

// TODO: Implement when different Yummizi's users can invite each other (Consider table relationship)
//@Entity(tableName = "friends")
//data class friend(
//    @PrimaryKey
//    val friendId: Uuid,
//    val name: String,
//    val photoUrl: String?,
//    val companySize: Int,
//)


@Entity(tableName = "restaurants")
data class Restaurant(  // TODO: This is the entity. And the domain should be another with the status
    @PrimaryKey
    @ColumnInfo(name ="id")
    val id: Uuid,

    @ColumnInfo(name ="name")
    val name: String,

    @ColumnInfo(name ="logoUrl")
    val logoUrl: String,

    @ColumnInfo(name ="sectionType")
    val sectionType: String,

    @ColumnInfo(name ="dishesToShowLink")
    val dishesToShowLink: List<String>,

    // 💡 Tip: There is no built-in standard Multiplatform spatial Coordinate primitive in Android Jetpack/Room yet.
    // Keeping raw primitives (Double) or creating a simple embedded @Embedded data class remains the industry standard.
    @ColumnInfo(name ="latitude")
    val latitude: Double,     // TO
    // DO
    @ColumnInfo(name ="longitude")
    val longitude: Double, //

    @ColumnInfo(name ="phoneNumberToPay")
    val phoneNumberToPay: String? = null,

    @ColumnInfo(name ="phoneNumberToContact")
    val phoneNumberToContact: String,

    @ColumnInfo(name ="paymentQrUrl")
    val paymentQrUrl: String? = null,

    // val maxWaitingTimeInMinutes: Int, // TODO: For now the default time is 15 minutes, but we must implement this attribute
    //val description: String,
)

@Entity(
    tableName = "order_selections",
    primaryKeys = ["dishId", "complementaryDishId"],
    foreignKeys = [
        ForeignKey(
            entity = Order::class,    /// Order::class,?
            parentColumns = ["orderId"], ///["orderId"],?
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["orderId"])] // Indexing FK column speeds up child joins
)
data class OrderSelection(

    @ColumnInfo(name ="orderId")
    val orderId: String,

    @ColumnInfo(name ="complementaryDishId")
    val complementaryDishId: Uuid= Uuid.NIL,

    @ColumnInfo(name ="dishId")
    val dishId: Uuid,

    @ColumnInfo(name ="dishName")
    val dishName: String,   // TODO: Change it by "selectionName"

    @ColumnInfo(name ="quantity")
    val quantity: Int,

    @ColumnInfo(name ="historicalUnitPriceInCents")
    val historicalUnitPriceInCents: Int // It's just local, it doesn't go in the payload to send.
)

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = Request::class,
            parentColumns = ["requestId"],
            childColumns = ["requestId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["requestId"], unique = true)] // Enforces 1-to-1 relationship & speeds up queries
)
data class Order(   // TODO: Include a some kind of "OrderStatus" that defines if the order is ready (no null values on its attributes) or not
    @PrimaryKey
    @ColumnInfo(name ="orderId")
    val orderId: String,

    @ColumnInfo(name ="requestId")
    val requestId: String,

    @ColumnInfo(name ="expectedArrivalDatetime")
    val expectedArrivalDatetime: Instant? = null,

    @ColumnInfo(name ="paymentImageRelativePath")
    val paymentImageRelativePath: String? = null,

    @ColumnInfo(name ="localOrderCreationTimestamp")
    val localOrderCreationTimestamp: Instant? = null,   // TODO: Consider the possibility that the time starts since the user enters to the food selection

    @ColumnInfo(name ="totalPriceInCentsFromBackend")
    val totalPriceInCentsFromBackend: Int? = null,

    @ColumnInfo(name ="extraCommentaries")
    val extraCommentaries: String? = null
//    val paymentMethod: String // TODO
)

@Entity(
    tableName = "requests",
    indices = [
        Index(value = ["restaurantId", "userId", "status"]) // 🚀 The "Speed" Button
    ]
)
data class Request(
    @PrimaryKey
    @ColumnInfo(name ="requestId")
    val requestId: String,

    @ColumnInfo(name ="restaurantId")
    val restaurantId: Uuid,

    @ColumnInfo(name ="status")
    val status: RequestStatusToken,

    @ColumnInfo(name ="userId")
    val userId: Uuid,   // TODO: Remove this parameter, so it's only lives in "LoggedUser". This change also incude modification in DAOs, because they shouldn't depen of the user ID

    @ColumnInfo(name ="onQueuedTimestamp")
    val onQueuedTimestamp: Long? = null,   // TODO: Convert to Instant type

    @ColumnInfo(name ="onContinueAcceptanceLocalTimestamp")
    val onContinueAcceptanceLocalTimestamp: Instant? = null,    //TODO: Change the meaning of its name to "onReadyRemoteTimeStamp"

    @ColumnInfo(name ="arrivalTimeInMinutes")
    val arrivalTimeInMinutes: Int,
    // val specifics: List<AvailableSpecificsToken>, // TODO
)

data class RequestUpdate(   // TODO: It goes in the domain, not entity
    val requestId: String,
//    val restaurantId: Uuid,
    val newStatus: RequestStatusToken
)


@Entity(
    tableName = "menu_pages",
    foreignKeys = [
        ForeignKey(
            entity = Restaurant::class,
            parentColumns = ["id"],
            childColumns = ["restaurantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["restaurantId"])]
)
data class MenuPage(
    @PrimaryKey
    @ColumnInfo(name ="id")
    val id: Uuid,

    @ColumnInfo(name ="restaurantId")
    val restaurantId: Uuid,

    @ColumnInfo(name ="imageUrl")
    val imageUrl: String,

    @ColumnInfo(name ="pageNumber")
    val pageNumber: Int // Used for sorting in the Pager
)


@Entity(
    tableName = "dishes",
    foreignKeys = [
        ForeignKey(
            entity = MenuPage::class,
            parentColumns = ["id"],
            childColumns = ["menuPageId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["menuPageId"])]
)
data class Dish(
    @PrimaryKey
    @ColumnInfo(name ="id")
    val id: Uuid,

    @ColumnInfo(name ="menuPageId")
    val menuPageId: Uuid,

    @ColumnInfo(name ="availableComplementaryDishIds")
    val availableComplementaryDishIds: List<Uuid> = emptyList(),

    @ColumnInfo(name ="name")
    val name: String,

    @ColumnInfo(name ="imageUrl")
    val imageUrl: String,

    @ColumnInfo(name ="priceCents")
    val priceCents: Int,

    @ColumnInfo(name ="prepTime")
    val prepTime: Int, // In minutes

    @ColumnInfo(name ="isOutOfStock")
    val isOutOfStock: Boolean = false,

    @ColumnInfo(name ="coordinateXInMenu")
    val coordinateXInMenu: Double,  //TODO: Later change it by section area of the menu image (rectangles), not by the a small button

    @ColumnInfo(name ="coordinateYInMenu")
    val coordinateYInMenu: Double,

    @ColumnInfo(name ="description")
    val description: String,

    @ColumnInfo(name ="type")
    val type: DishTypeToken = DishTypeToken.MAIN, /// The types are "COMPLEMENTARY" or "MAIN", so if it's "MAIN", takes it own price
//    val conditionalPiceCents: Int? = null, /// Use it when different complementary dishes set a different price to the main dish   /// It defines the price of "MAIN" + "COMPLEMENTARY". This means that the complementary dish decides the set price.
)


@Entity(
    tableName = "preferences",
    indices = [Index(value = ["userId"])] // High Performance Index
)
data class Preference(
    @PrimaryKey
    @ColumnInfo(name ="id")
    val id: Uuid,

    @ColumnInfo(name ="userId")
    val userId: Uuid,

    @ColumnInfo(name ="title")
    val title: String,

    @ColumnInfo(name ="description")
    val description: String,

    @ColumnInfo(name ="color")
    val color: String,

    @ColumnInfo(name ="noSyncError")
    val noSyncError: Boolean = false,

    @ColumnInfo(name ="createdAt")
    val createdAt: Long = System.currentTimeMillis()
)