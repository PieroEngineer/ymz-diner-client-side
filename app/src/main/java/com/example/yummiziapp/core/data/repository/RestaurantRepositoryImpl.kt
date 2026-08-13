package com.example.yummiziapp.core.data.repository

import android.util.Log
import com.example.yummiziapp.core.data.di.IoDispatcher
import com.example.yummiziapp.core.data.local.dao.MenuDao
import com.example.yummiziapp.core.data.local.dao.RequestDao
import com.example.yummiziapp.core.data.local.dao.RestaurantDao
import com.example.yummiziapp.core.data.local.entities.Dish
import com.example.yummiziapp.core.data.local.entities.Restaurant
import com.example.yummiziapp.core.data.local.model.RestaurantWithStatus
import com.example.yummiziapp.core.data.network.api.RestaurantApi
import com.example.yummiziapp.core.data.network.model.RequestStatusMessageDto
import com.example.yummiziapp.core.data.network.model.toEntity
import com.example.yummiziapp.core.data.network.websocket.AblyDataSource
import com.example.yummiziapp.core.data.network.websocket.mappers.toRawJsonString
import com.example.yummiziapp.features.restaurant.domain.repository.RestaurantRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.uuid.Uuid

class RestaurantRepositoryImpl @Inject constructor(
    private val requestDao: RequestDao,
    private val restaurantDao: RestaurantDao,
    private val menuDao: MenuDao,

    private val yummiziApi: RestaurantApi,
    private val dataSource: AblyDataSource,

    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val json: Json
) : RestaurantRepository {

    override fun observeDishes(restaurantId: Uuid): Flow<List<Dish>> = menuDao.observeDishesByRestaurant(restaurantId)

    override fun observeRestaurantMenuWithItsDishes(id: Uuid) = menuDao.observeRestaurantMenuWithItsDishes(id)

    override fun observeMenuPagesByRestaurant(restaurantId: Uuid) = menuDao.observeMenuPagesByRestaurant(restaurantId)

    override suspend fun clearAllRestaurantsAndRequests() {
        /// Does it need a transaction?
        restaurantDao.clearRestaurantsFromFeed()
        requestDao.clearAll()
        Log.d("MyActivity", "🧹  Already cleaned\n")
    }

    override suspend fun fetchPaymentInfo(restaurantId: Uuid) {
        withContext(ioDispatcher) { /// Is "withContext(...)" necessary here?
            try {
                val response = yummiziApi.getPaymentInfo(restaurantId.toString())
                if (response.isSuccessful) {
                    response.body()?.let { paymentInfo ->
                        restaurantDao.updatePaymentInfo(
                            restaurantId = restaurantId,
                            phoneToPay = paymentInfo.phoneNumberToPay,
                            phoneToContact = paymentInfo.phoneNumberToContact,
                            qrUrl = paymentInfo.paymentQrUrl
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("MyActivity", "Failed to fetch payment info for restaurant: $restaurantId", e)
            }
        }
    }

    // Fetch from remote and save to Room
    override suspend fun syncFeedRestaurants() = withContext(ioDispatcher) {
        try {
            val userId = Uuid.parse("00000000-0000-4000-a000-000000000000")
            Log.d("MyActivity", "🚀 Starting API call wit this user ID ${userId}...")
            val remoteData = yummiziApi.getFeedRestaurants(userId.toString())
            Log.d("MyActivity", "✅📩 Data fetched from API to sync the feed:\n$remoteData\n")
            restaurantDao.insertRestaurants(remoteData.map { it.toEntity() })
            Log.d("MyActivity", "✅↩️ Insert was successful \n")

            Unit
        } catch (e: Exception) {
            Log.e("MyActivity", "🚩☁️ Error in sync: ${e.message}")
            e.printStackTrace()
        }
    }

    // The UI stays "hooked" to this flow. It updates automatically when Room changes.
    override fun observeRestaurantsInUserScope(): Flow<List<Restaurant>> { // This is to get all restaurants in the table; in other words, to get the restaurants' data in of the feed
        return restaurantDao.getFeedRestaurants()
    }

    override fun observeRestaurantById(id: Uuid) = restaurantDao.observeRestaurantById(id)

    override fun observeRestaurantsByIds(restaurantIds: List<Uuid>): Flow<List<Restaurant>> {
        return restaurantDao.observeRestaurantsByIds(restaurantIds)
    }


    override suspend fun refreshRestaurantMenu(restaurantId: Uuid) = withContext(ioDispatcher) {
        try {
            // Network Call
            Log.d("MyActivity", "1")
            val remoteMenu = yummiziApi.getRestaurantMenu(restaurantId.toString())

            // Mapping DTOs to Entities (The "Transformation")
            Log.d("MyActivity", "2")
            val pageEntities = remoteMenu.menuPages.map { it.toEntity(restaurantId.toString()) }    /// Is it necessary to include a the restaurant ID? isn't it already in the arrived data?
            Log.d("MyActivity", "3")
            val dishEntities = remoteMenu.dishes.map { it.toEntity() }
            Log.d("MyActivity", "✅🍽️  Syncing menu for restaurant: $restaurantId was succesful")


            // Database Insertion (Atomic Transaction)
            menuDao.clearAndInsertMenu(restaurantId,pageEntities, dishEntities)
            Log.d("MyActivity", "✅📇  Syncing menu for restaurant: $restaurantId was succesful")

            Unit

        } catch (e: Exception) {
            // Elegant Error Handling
            Log.e("MyActivity", "🚩📇  Failed to sync menu for restaurant: $restaurantId", e)
            // Note: In Offline-first, we don't throw the error;
            // the UI will simply show the last cached data.
            Unit
        }
    }

    // In UserRepository / RestaurantRepository
    override fun observePendingRestaurantsWithStatus(): Flow<List<RestaurantWithStatus>> = flow {  // "Pending" means "QUEUED" and "ACCEPTED"
        // 1. Get current ID (keeping your mock for now, but using the DAO)
        val currentUserId = Uuid.parse("00000000-0000-4000-a000-000000000000")

        // 🚀 Stream emission strategy:
        // 1. Emit local Room Flow directly to UI (SSOT)
        // 2. Concurrently consume WebSocket updates to sync Room DB

        coroutineScope {
            // Launch the sync in the background
            launch { syncWebSocketToRoom() } // TODO (#527): Try to move this to the request Repo. In the end, there is no problem if the user's mobile listen when they are not in the screen

            // Immediately start emitting data from Room
            emitAll(restaurantDao.observePendingRestaurantsWithStatus(currentUserId))
        }
    }

    override fun observePendingRestaurantWithStatusById(restaurantId: Uuid): Flow<RestaurantWithStatus?> = flow {
        val currentUserId = Uuid.parse("00000000-0000-4000-a000-000000000000")

        // 🚀 Stream emission strategy:
        // 1. Emit local Room Flow directly to UI (SSOT)
        // 2. Concurrently consume WebSocket updates to sync Room DB

        coroutineScope {
            // Launch the sync in the background
            launch { syncWebSocketToRoom() }    // TODO (#527)

            // Immediately start emitting data from Room
            emitAll(restaurantDao.observePendingRestaurantWithStatusById(currentUserId, restaurantId))
        }

    }

    override suspend fun getRestaurantById(id: Uuid) = restaurantDao.getRestaurantById(id)

    override fun observePendingRestaurants(): Flow<List<Restaurant>> {
        // In a real app, this ID comes from your UserSessionManager
        val currentUserId = Uuid.parse("00000000-0000-4000-a000-000000000000")

        return restaurantDao.observePendingRestaurants(currentUserId)
    }

    override fun observeNoPendingRestaurantWithStatus(): Flow<RestaurantWithStatus?> {
        val currentUserId = Uuid.parse("00000000-0000-4000-a000-000000000000")
        return restaurantDao.observeNoPendingRestaurantWithStatus(currentUserId)
    }

    private suspend fun syncWebSocketToRoom() = withContext(ioDispatcher) {
        val userId = "00000000-0000-4000-a000-000000000000"
        val channelName = "request:$userId"

        Log.d("MyActivity", "🔎  App is going to subscribe to the WebSocket channel: $channelName ...")

        dataSource.subscribeToChannel(channelName)
            .collect { rawMessage ->
                // Safely extracts JSON String regardless of Ably's internal type
                val rawJsonString = rawMessage.toRawJsonString() ?: return@collect

                Log.d("MyActivity", "🔎📩  Received a WebSocket String raw message: $rawJsonString (${rawJsonString::class.simpleName})")

                // 🛡️ STEP 1: Parse payload safely
                val dtoResult = runCatching {
                    json.decodeFromString<RequestStatusMessageDto>(rawJsonString)
                }

                dtoResult.onSuccess { dto ->
                    // 🛡️ STEP 2: Write to Room DB safely
                    try {
                        val requestUpdate = dto.toEntity()
                        // TODO: Verify here if the transition between request status is valid (e.g. Accepted only is allowed if the previous state was QUEUED)

                        requestDao.updateRequestStatus(requestUpdate.requestId, requestUpdate.newStatus)
                    } catch (e: Exception) {
                        Log.e("MyActivity", "🚩  Database write failure", e)
                    }
                }.onFailure { error ->
                    Log.w("MyActivity", "🚩  Malformed WebSocket payload dropped", error)
                }
            }
    }
}


// For global Websockets

// 1. Keep one sync job active
//private var syncJob: Job? = null
//
//// 2. Start it once when needed (or in init)
//private fun startGlobalSync() {
//    if (syncJob?.isActive == true) return
//    syncJob = CoroutineScope(ioDispatcher).launch {
//        syncWebSocketToRoom()
//    }
//}
//
//// 3. The observer is now "Clean" (Only reads DB)
//override fun observePendingRestaurantsWithStatus(): Flow<List<RestaurantWithStatus>> {
//    startGlobalSync() // Ensure sync is running
//    return restaurantDao.observePendingRestaurantsWithStatus(currentUserId)
//}