package com.example.yummiziapp.core.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.yummiziapp.core.data.local.model.AvailableSpecificsToken
import com.example.yummiziapp.core.data.local.model.RequestStatusToken
//X import com.example.yummiziapp.features.request.data.model.QueueStatus
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "logged_user")
data class LoggedUser(
    @PrimaryKey val id: Uuid, // The "userId"
    val name: String,
    val favoriteRestaurants: List<Uuid> = emptyList(),
    val companySize: Int,
    val specifics: List<AvailableSpecificsToken>,
)

// TODO: Implement when different Yummizi's users can invite each other (Consider table relationship)
//@Serializable
//@Entity(tableName = "friends")
//data class friend(
//    @PrimaryKey val friendId: Uuid,
//    val name: String,
//    val photoUrl: String?,
//    val companySize: Int,
//)

@Serializable
@Entity(tableName = "restaurants")
data class Restaurant(  // TODO: This is the entity. And the domain should be another with the status
    @PrimaryKey val id: Uuid,
    val name: String,
    val logoUrl: String,
    val sectionType: String,
    val dishesToShowLink: List<String>,
    // 💡 Tip: There is no built-in standard Multiplatform spatial Coordinate primitive in Android Jetpack/Room yet.
    // Keeping raw primitives (Double) or creating a simple embedded @Embedded data class remains the industry standard.
//    val latitude: Double?,     // TODO
//    val longitude: Double?, //
    val phoneNumberToPay: Int? = null,
    val phoneNumberToContact: Int? = null,
    val paymentQrUrl: String? = null
    // val maxWaitingTimeInMinutes: Int, // TODO: For now the default time is 15 minutes, but we must implement this attribute
    //val description: String,
)

@Entity(
    tableName = "order_selections",
    primaryKeys = ["requestId", "dishId"],
    foreignKeys = [
        ForeignKey(
            entity = Request::class,
            parentColumns = ["requestId"],
            childColumns = ["requestId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["requestId"]), Index(value = ["dishId"])]
)
data class OrderSelection(  /// Should I include the restaurant here? I ask because it's already in the request and it's associated
    val requestId: Uuid,
    val dishId: Uuid,
    val dishName: String,
    val quantity: Int,
    val historicalUnitPriceInCents: Int // It's just local, it doesn't go in the payload to send.
//    val paymentMethod: String // TODO
)

@Entity(
    tableName = "requests",
    indices = [
        Index(value = ["restaurantId", "userId", "status"]) // 🚀 The "Speed" Button
    ]
)
@Serializable
data class Request(
    @PrimaryKey val requestId: Uuid,
    val restaurantId: Uuid,
    val status: RequestStatusToken,
    val userId: Uuid,   // TODO: Remove this parameter, so it's only lives in "LoggedUser"
    val localCreationTimestamp: Long,
    val arrivalTimeInMinutes: Int,
    // val specifics: List<AvailableSpecificsToken>, // TODO
)

data class RequestUpdate(
    val requestId: Uuid,
//    val restaurantId: Uuid,
    val newStatus: RequestStatusToken
)

@Serializable
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

@Serializable
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
    val name: String,
    val imageUrl: String,
    val priceCents: Int,
    val prepTime: Int, // In minutes
    val isOutOfStock: Boolean = false,
    val menuPageId: Uuid,
    val coordinateXInMenu: Double,  //TODO: Later change it by section area of the menu image (rectangles), not by the a small button
    val coordinateYInMenu: Double,
    val description: String
)

@Serializable
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