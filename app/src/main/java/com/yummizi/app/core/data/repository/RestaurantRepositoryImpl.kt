package com.yummizi.app.core.data.repository

import android.util.Log
import com.yummizi.app.core.data.di.IoDispatcher
import com.yummizi.app.core.data.local.dao.LoggedUserDao
import com.yummizi.app.core.data.local.dao.MenuDao
import com.yummizi.app.core.data.local.dao.OrderDao
import com.yummizi.app.core.data.local.dao.RequestDao
import com.yummizi.app.core.data.local.dao.RestaurantDao
import com.yummizi.app.core.data.local.entities.Dish
import com.yummizi.app.core.data.local.entities.Order
import com.yummizi.app.core.data.local.entities.OrderSelection
import com.yummizi.app.core.data.local.entities.Restaurant
import com.yummizi.app.core.data.local.model.RestaurantWithStatus
import com.yummizi.app.core.data.network.api.RestaurantApi
import com.yummizi.app.core.data.network.model.CancelRequestMessageDto
import com.yummizi.app.core.data.network.model.UpdateRequestStatusMessageDto
import com.yummizi.app.core.data.network.model.toEntity
import com.yummizi.app.core.data.network.websocket.AblyDataSource
import com.yummizi.app.core.data.network.websocket.mappers.toRawJsonString
import com.yummizi.app.features.restaurant.domain.repository.RestaurantRepository
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
    private val orderDao: OrderDao,

    private val loggedUserDao: LoggedUserDao,
    private val yummiziApi: RestaurantApi,
    private val dataSource: AblyDataSource,

    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val json: Json
) : RestaurantRepository {

    override fun observeDishes(restaurantId: Uuid): Flow<List<Dish>> = menuDao.observeDishesByRestaurant(restaurantId)

//    override fun observeRestaurantMenuWithItsDishes(id: Uuid) = menuDao.observeRestaurantMenuWithItsDishes(id)

    override fun observeRestaurantMenuWithAvailableDishes(id: Uuid) = menuDao.observeRestaurantMenuWithAvailableDishes(id)

    override fun observeMenuPagesByRestaurant(restaurantId: Uuid) = menuDao.observeMenuPagesByRestaurant(restaurantId)

    override suspend fun generateNewOrder() = withContext(ioDispatcher) {

        /// is it necessary to delete all the previous orders here? Maybe not because the I already remove the request later.

        val readyRequest = requestDao.getLatestReadyRequest()
            ?: throw IllegalStateException("🚩  No READY request found when generating order order")

        val newBaseOrder = Order(
            orderId = readyRequest.requestId,   // TODO: Bring it from backend (Perhaps from the acceptance response) /// (#294) Is it ok that I use the request ID here?
            requestId = readyRequest.requestId,
//            totalPriceInCentsFromBackend = totalPriceInCentsFromRemote, //TODO: Bring this value from remote
        )

        orderDao.createOrder(newBaseOrder)
    }

    override fun observeOrderSelections() = orderDao.observeOrderSelections()

    override suspend fun deleteOrderSelectionById(dishId: Uuid, complementaryDishId: Uuid) = orderDao.deleteOrderSelectionById(dishId, complementaryDishId)

    override suspend fun getQuantityOfASelectionById(dishId: Uuid, complementaryDishId: Uuid) = orderDao.getQuantityOfASelectionById(dishId, complementaryDishId)

    override suspend fun updateOrderSelectionQuantity(dishId: Uuid, complementaryDishId: Uuid, delta: Int) = orderDao.updateSelectionQuantityById(dishId, complementaryDishId, delta)

//    override suspend fun checkIfThereAreSelections() = orderDao.checkIfThereAreSelections()

    override suspend fun includeASelection(dishId: Uuid, complementaryDishId: Uuid?) = withContext(ioDispatcher) {

        Log.d("FoodSelectionViewModel", "🔎🍽️  A food was selected with complementary dish ID $complementaryDishId")

        // TODO: If "complementaryDishId" arrives as "NIL", use the conditional price

        val hasComplementaryDish = complementaryDishId != null
        Log.d("FoodSelectionViewModel", "🔎🍽️  Has complementary dish? $hasComplementaryDish")

        val temporalComplementaryDishIId = if(hasComplementaryDish) complementaryDishId else Uuid.NIL

        // Check if the selection already exists
        val doesSelectionAlreadyExist = orderDao.checkIfSelectionExists(dishId, temporalComplementaryDishIId)

        // If it exists, just increase one unit
        if (doesSelectionAlreadyExist) {
            Log.d("FoodSelectionViewModel", "🔎🍽️  Selection already exists")

            orderDao.updateSelectionQuantityById(dishId, temporalComplementaryDishIId, 1)
        }
        else{
            Log.d("FoodSelectionViewModel", "🔎🍽️  Selection doesn't exist")

            val currentOrderId = orderDao.getCurrentOrderId()
                ?: throw IllegalStateException("No order found when generating order")

            Log.d("FoodSelectionViewModel", "🔎🍽️  Current order ID $currentOrderId")

            val mainDish = menuDao.getDishCostingById(dishId)

            Log.d("FoodSelectionViewModel", "🔎🍽️  Main dish $mainDish")

            /// Check the types if necessary. I didn't put it jet because it was verified before.

            val mainDishName = mainDish.name

            val selectionName = if(hasComplementaryDish) "$mainDishName + ${menuDao.getDishNameById(temporalComplementaryDishIId)}" else mainDishName

            val newOrderSelection = OrderSelection(
                orderId = currentOrderId,
                complementaryDishId = if (hasComplementaryDish) complementaryDishId else Uuid.NIL,
                dishId = dishId,
                dishName = selectionName,   // TODO: Replace the attribute's name by "selectionName"
                quantity = 1,
                historicalUnitPriceInCents = mainDish.priceCents // It's just local, it doesn't go in the payload to send.
            )

            orderDao.insertNewSelection(newOrderSelection)
        }

    }

    override suspend fun getDishNameById(dishId: Uuid) = menuDao.getDishNameById(dishId)

    override suspend fun getComplementaryDishesById(dishId: Uuid): List<Uuid> {
        return menuDao.getComplementaryDishIdsByDishId(dishId)?.availableComplementaryDishIds ?: emptyList()
    }

    // It doesn't bring out-of-stock dishes
    override suspend fun getAvailableReferencesByDishIds(dishIds: List<Uuid>) = menuDao.getAvailableReferencesByDishIds(dishIds)

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
                Log.d("MyActivity", "✅📩 Data fetched from API to sync the Payment data:\n$response\n")
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
            val userId = loggedUserDao.getCurrentUserId() ?: throw IllegalStateException("No logged-in user found")

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
        return restaurantDao.observeFeedRestaurants()
    }

    override fun observeRestaurantById(id: Uuid) = restaurantDao.observeRestaurantById(id)

    override fun observeRestaurantsByIds(restaurantIds: List<Uuid>): Flow<List<Restaurant>> {
        return restaurantDao.observeRestaurantsByIds(restaurantIds)
    }


    override suspend fun refreshRestaurantMenu(restaurantId: Uuid) = withContext(ioDispatcher) {
        try {
            // Network Call
            val remoteMenu = yummiziApi.getRestaurantMenu(restaurantId.toString())

            // Mapping DTOs to Entities (The "Transformation")
            Log.d("MyActivity", "🔎 The remote data is: $remoteMenu")
            val pageEntities = remoteMenu.menuPages.map { it.toEntity(restaurantId.toString()) }    /// Is it necessary to include a the restaurant ID? isn't it already in the arrived data?
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

        // 🚀 Stream emission strategy:
        // 1. Emit local Room Flow directly to UI (SSOT)
        // 2. Concurrently consume WebSocket updates to sync Room DB

        coroutineScope {
            // Launch the sync in the background
            launch { syncWebSocketToRoom() } // TODO (#527): Try to move this to the request Repo. In the end, there is no problem if the user's mobile listen when they are not in the screen

            // Immediately start emitting data from Room
            emitAll(restaurantDao.observePendingRestaurantsWithStatus())
        }
    }

    override fun observePendingRestaurantWithStatusById(restaurantId: Uuid): Flow<RestaurantWithStatus?> = flow {

        // 🚀 Stream emission strategy:
        // 1. Emit local Room Flow directly to UI (SSOT)
        // 2. Concurrently consume WebSocket updates to sync Room DB

        coroutineScope {
            // Launch the sync in the background
            launch { syncWebSocketToRoom() }    // TODO (#527)

            // Immediately start emitting data from Room
            emitAll(restaurantDao.observePendingRestaurantWithStatusById(restaurantId))
        }

    }

    override suspend fun getRestaurantById(id: Uuid) = restaurantDao.getRestaurantById(id)

    override fun observePendingRestaurants(): Flow<List<Restaurant>> {

        return restaurantDao.observePendingRestaurants()
    }

    override fun observeNoPendingRestaurantWithStatus(): Flow<RestaurantWithStatus?> {
        return restaurantDao.observeNoPendingRestaurantWithStatus()
    }

    private suspend fun syncWebSocketToRoom() = withContext(ioDispatcher) {
        val userId = loggedUserDao.getCurrentUserId() ?: throw IllegalStateException("No logged-in user found")
        val channelName = "request:$userId"

        Log.d("MyActivity", "🔎  App is going to subscribe to the WebSocket channel: $channelName ...")

        dataSource.subscribeToChannel(channelName)
            .collect { rawMessage ->

                Log.d("MyActivity", "🔎📩  Received a WebSocket message whose event name is:")
                Log.d("MyActivity", "🔎📩  ${rawMessage.name}")

                when (rawMessage.name) {
                    "request-status-updated" -> {
                        // Safely extracts JSON String regardless of Ably's internal type
                        val rawJsonDataString = rawMessage.toRawJsonString() ?: return@collect

                        Log.d("MyActivity", "🔎📩  Received a WebSocket for 'request-status-updated' String raw message: $rawJsonDataString (${rawJsonDataString::class.simpleName})")

                        // 🛡️ STEP 1: Parse payload safely
                        val dtoResult = runCatching {
                            json.decodeFromString<UpdateRequestStatusMessageDto>(rawJsonDataString)
                        }

                        dtoResult
                            .onSuccess { dto ->
                                // 🛡️ STEP 2: Write to Room DB safely
                                try {
                                    val requestUpdate = dto.toEntity()
                                    // TODO: Verify here if the transition between request status is valid (e.g. Accepted only is allowed if the previous state was QUEUED)

                                    requestDao.updateRequestStatus(requestUpdate.requestId, requestUpdate.newStatus)
                                } catch (e: Exception) {
                                    Log.e("MyActivity", "🚩  Database write failure in request-status-updated", e)
                                }
                            }
                            .onFailure { error ->
                            Log.w("MyActivity", "🚩  Malformed WebSocket payload dropped for request-status-updated", error)
                        }
                    }
                    "request-canceled" -> { // TODO: Include it in the home too
                        // Safely extracts JSON String regardless of Ably's internal type
                        val rawJsonDataString = rawMessage.toRawJsonString() ?: return@collect

                        Log.d("MyActivity", "🔎📩  Received a WebSocket for 'request-canceled' String raw message: $rawJsonDataString (${rawJsonDataString::class.simpleName})")

                        // 🛡️ STEP 1: Parse payload safely
                        val dtoResult = runCatching {
                            json.decodeFromString<CancelRequestMessageDto>(rawJsonDataString)
                        }

                        dtoResult
                            .onSuccess { dto ->
                                // 🛡️ STEP 2: Write to Room DB safely
                                try {
                                    val requestUpdate = dto.toDomain()
                                    requestDao.deleteRequestById(Uuid.parse(requestUpdate.requestId))
                                }
                                catch (e: Exception){
                                    Log.e("MyActivity", "🚩  Error when trying to remove a value from the room", e)
                                }
                            }
                            .onFailure { error ->
                                Log.w("MyActivity", "🚩  Malformed WebSocket payload dropped for request-canceled", error)
                            }



                    }
                    else -> { return@collect }


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