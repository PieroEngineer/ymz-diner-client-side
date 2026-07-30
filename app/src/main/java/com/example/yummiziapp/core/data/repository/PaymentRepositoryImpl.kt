package com.example.yummiziapp.core.data.repository

import javax.inject.Inject
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.yummiziapp.core.data.di.IoDispatcher
import com.example.yummiziapp.core.data.local.dao.LoggedUserDao
import com.example.yummiziapp.core.data.local.dao.MenuDao
import com.example.yummiziapp.core.data.local.dao.PaymentDao
import com.example.yummiziapp.core.data.local.dao.RequestDao
import com.example.yummiziapp.core.data.local.entities.OrderSelection
import com.example.yummiziapp.core.data.network.api.PaymentApi
import com.example.yummiziapp.core.data.network.model.mappers.createSendOrderDto
import com.example.yummiziapp.core.data.network.model.mappers.toDtoList
import com.example.yummiziapp.features.payment.domain.repository.PaymentRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.uuid.Uuid

/// Should I put a singleton in every repository?
class PaymentRepositoryImpl @Inject constructor(
    private val paymentDao: PaymentDao,
    private val requestDao: RequestDao,
    private val menuDao: MenuDao,
    private val loggedUserDao: LoggedUserDao,
    private val yummiziApi: PaymentApi,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PaymentRepository {

    override suspend fun selectOrder(orderMap: Map<Uuid, Int>): Result<Int> = withContext(ioDispatcher) {
        runCatching {
            // Get the current "READY" request and extract its ID and the Restaurant ID
            val readyRequest = requestDao.getLatestReadyRequest()
                ?: throw IllegalStateException("No READY request found")
            
            val loggedUser = loggedUserDao.getUser(Uuid.parse("00000000-0000-4000-a000-000000000000"))
                ?: throw IllegalStateException("No logged user found")

            val userId = loggedUser.id

            // Get dish details to get historical price
            val dishIds = orderMap.keys.toList()
            val dishes = menuDao.getDishesByIds(dishIds)
            val dishPriceMap = dishes.associateBy({ it.id }, { it.priceCents }) // TODO: Optimze this double association
            val dishNameMap = dishes.associateBy({ it.id }, { it.name })

            // Convert the map into a list of "OrderSelection" entities
            val orderSelections = orderMap.map { (dishId, quantity) ->
                OrderSelection(
                    requestId = readyRequest.requestId,
                    dishId = dishId,
                    quantity = quantity,
                    dishName = dishNameMap[dishId] ?: "unnamed dish",
                    historicalUnitPriceInCents = dishPriceMap[dishId] ?: 0
                )
            }

            // Save locally
            paymentDao.insertOrderSelections(orderSelections)

            // Convert the list of "OrderSelection" into a list of "OrderSelectionDto"
            val orderSelectionDtos = orderSelections.toDtoList()

            // Add the list of "OrderSelectionDto" into a "sendOrderReadyToBePaidDto"
            val payload = createSendOrderDto(
                requestId = readyRequest.requestId,
                restaurantId = readyRequest.restaurantId,
                userId = userId,
                userName = "Sir Mock User p", //readyRequest.userName,
                arrivalTime = readyRequest.arrivalTimeInMinutes,
                groupSize = loggedUser.companySize + 1,
                selections = orderSelectionDtos
            )

            Log.d("MyActivity", "⏫  Payload of the request: $payload")

            // POST the sendOrderReadyToBePaidDto with the api function "selectOrder"
            val response = yummiziApi.selectOrder(payload)

            if (response.isSuccessful) {
                // Receive the response with the total price and return it
                response.body()?.totalPriceInCents 
                    ?: throw IllegalStateException("Empty response body")
            } else {
                throw Exception("API call failed: ${response.code()} ${response.message()}")
            }
        }
    }

    override fun observeCurrentOrder(): Flow<List<OrderSelection>> = paymentDao.observeOrderSelections()

    override suspend fun getCurrentOrder(): List<OrderSelection> = withContext(ioDispatcher) {
        paymentDao.getOrderSelections()
    }

//    override suspend fun getPaymentImageFromInternalStorage(filename: String): Result<File> = withContext(ioDispatcher) {
//        //  TODO
//    }

    // ... removeCurrenImageFromInternalStorage() ...
    // TODO

    override suspend fun savePaymentImageToInternalStorage(uri: Uri): Result<File> = withContext(ioDispatcher) {
        runCatching {
            // Generation handler
            val contentResolver = context.contentResolver

            // Determine file extension or default to jpg
            val mimeType = contentResolver.getType(uri)
            val extension = if (mimeType?.contains("png") == true) "png" else "jpg"

            // Shaping output
            val targetFile = File(context.cacheDir, "selected_image_${Uuid.random()}.$extension")   // TODO: Change that random UUID by a timestamp, so we can use it later in the logic to get or delete it.

            // Byte Streaming and Copying
            contentResolver.openInputStream(uri)?.use { inputStream ->
                targetFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: throw IllegalStateException("Unable to open input stream for Uri: $uri")


//            // Create the entity using the file's absolute path
//            val payment = PaymentEntity(
//                amountInCents = amountCents,
//                imagePath = targetFile.absolutePath //  "Houses/cache/payment_xyz.jpg"
//            )
//
//            // Save the text path to Room
//            paymentDao.insertPayment(payment)

            targetFile
        }
    }
}