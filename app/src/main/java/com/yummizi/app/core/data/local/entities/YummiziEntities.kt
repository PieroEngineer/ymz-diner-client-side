package com.yummizi.app.core.data.local.entities

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
    @PrimaryKey val id: Uuid, // The "userId"
    val name: String,
    val favoriteRestaurants: List<Uuid> = emptyList(),
    val companySize: Int,
    val specifics: List<AvailableSpecificsToken>,
)

// TODO: Implement when different Yummizi's users can invite each other (Consider table relationship)
//@Entity(tableName = "friends")
//data class friend(
//    @PrimaryKey val friendId: Uuid,
//    val name: String,
//    val photoUrl: String?,
//    val companySize: Int,
//)


@Entity(tableName = "restaurants")
data class Restaurant(  // TODO: This is the entity. And the domain should be another with the status
    @PrimaryKey val id: Uuid,
    val name: String,
    val logoUrl: String,
    val sectionType: String,
    val dishesToShowLink: List<String>,
    // 💡 Tip: There is no built-in standard Multiplatform spatial Coordinate primitive in Android Jetpack/Room yet.
    // Keeping raw primitives (Double) or creating a simple embedded @Embedded data class remains the industry standard.
    val latitude: Double,     // TODO
    val longitude: Double, //
    val phoneNumberToPay: String? = null,
    val phoneNumberToContact: String,
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
    val orderId: String,
    val complementaryDishId: Uuid= Uuid.NIL,
    val dishId: Uuid,
    val dishName: String,   // TODO: Change it by "selectionName"
    val quantity: Int,
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
    @PrimaryKey val orderId: String,
    val requestId: String,
    val expectedArrivalDatetime: Instant? = null,
    val paymentImageRelativePath: String? = null,
    val localOrderCreationTimestamp: Instant? = null,   // TODO: Consider the possibility that the time starts since the user enters to the food selection
    val totalPriceInCentsFromBackend: Int? = null,
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
    @PrimaryKey val requestId: String,
    val restaurantId: Uuid,
    val status: RequestStatusToken,
    val userId: Uuid,   // TODO: Remove this parameter, so it's only lives in "LoggedUser". This change also incude modification in DAOs, because they shouldn't depen of the user ID
    val localCreationTimestamp: Long,   // TODO: Convert to Instant type
    val localConfirmationTimestamp: Instant? = null,
    val arrivalTimeInMinutes: Int,
    // val specifics: List<AvailableSpecificsToken>, // TODO
)

data class RequestUpdate(
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
    @PrimaryKey val id: Uuid,
    val restaurantId: Uuid,
    val imageUrl: String,
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
    @PrimaryKey val id: Uuid,
    val menuPageId: Uuid,
    val availableComplementaryDishIds: List<Uuid> = emptyList(),
    val name: String,
    val imageUrl: String,
    val priceCents: Int,
    val prepTime: Int, // In minutes
    val isOutOfStock: Boolean = false,
    val coordinateXInMenu: Double,  //TODO: Later change it by section area of the menu image (rectangles), not by the a small button
    val coordinateYInMenu: Double,
    val description: String,
    val type: DishTypeToken = DishTypeToken.MAIN, /// The types are "COMPLEMENTARY" or "MAIN", so if it's "MAIN", takes it own price
//    val conditionalPiceCents: Int? = null, /// Use it when different complementary dishes set a different price to the main dish   /// It defines the price of "MAIN" + "COMPLEMENTARY". This means that the complementary dish decides the set price.
)


@Entity(
    tableName = "preferences",
    indices = [Index(value = ["userId"])] // High Performance Index
)
data class Preference(
    @PrimaryKey val id: Uuid,
    val userId: Uuid,
    val title: String,
    val description: String,
    val color: String,
    val noSyncError: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)