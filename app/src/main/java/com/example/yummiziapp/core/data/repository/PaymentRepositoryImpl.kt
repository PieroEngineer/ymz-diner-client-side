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

    override suspend fun selectOrder(orderMap: Map<Uuid, Int>)/*: Result<Unit>*/ = withContext(ioDispatcher) {
//        runCatching { /// Should I use runCatching?
            // Get the current "READY" request and extract its ID and the Restaurant ID
            val readyRequest = requestDao.getLatestReadyRequest()
                ?: throw IllegalStateException("No READY request found")
            
            val loggedUser = loggedUserDao.getUser(Uuid.parse("00000000-0000-4000-a000-000000000000"))
                ?: throw IllegalStateException("No logged user found")

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
//        }
    }

    override suspend fun confirmSelectedOrder() {    // TODO: Update the state of the request to PAID
        // Get the current "READY" request and extract its ID and the Restaurant ID
        val readyRequest = requestDao.getLatestReadyRequest()
            ?: throw IllegalStateException("No READY request found")

        val loggedUser = loggedUserDao.getUser(Uuid.parse("00000000-0000-4000-a000-000000000000"))
            ?: throw IllegalStateException("No logged user found")

        // Convert the list of "OrderSelection" into a list of "OrderSelectionDto"
        val orderSelectionDtos = paymentDao.getOrderSelections().toDtoList()

        // Sum of prices
        val totalPriceInCentsFromLocal = orderSelectionDtos.sumOf { it.historicalUnitPriceInCents * it.quantity }

        // Add the list of "OrderSelectionDto" into a "sendOrderReadyToBePaidDto"
        val payload = createSendOrderDto(   // TODO: change it to only receive the request and logged user directly to make it looks better
            requestId = readyRequest.requestId,
            restaurantId = readyRequest.restaurantId,
            userId = loggedUser.id,
            userName = "Sir Mock User p", //readyRequest.userName,
            arrivalTime = readyRequest.arrivalTimeInMinutes,
            groupSize = loggedUser.companySize + 1,
            selections = orderSelectionDtos
        )

        Log.d("MyActivity", "⏫  Payload of the request: $payload")

        // POST the sendOrderReadyToBePaidDto with the api function "selectOrder"
        val response = yummiziApi.selectOrder(payload)

        if (response.isSuccessful) {    // TODO: Also return the time when the customer should arrive, the client-side doesn't decide that
            requestDao.updateRequestStatus(readyRequest.requestId, RequestStatusToken.PAID)

            // Receive the response with the total price and return it
            val totalPriceInCentsFromRemote = response.body()?.totalPriceInCents
                ?: throw IllegalStateException("Empty response body")

            if (totalPriceInCentsFromLocal != totalPriceInCentsFromRemote) {
                Log.d("MyActivity", "\n🚩💲  The local price doesn't match with the remote price:\n  Local: $totalPriceInCentsFromLocal\n  Remote: $totalPriceInCentsFromRemote")
            }
            else{
                Log.d("MyActivity", "✅💲  The local price matches with the remote price")
            }

        } else {
            throw Exception("API call failed: ${response.code()} ${response.message()}")
        }
    }

    override fun observeCurrentOrder(): Flow<List<OrderSelection>> = paymentDao.observeOrderSelections()

    override suspend fun getCurrentOrder(): List<OrderSelection> = withContext(ioDispatcher) {  /// Is this "withContext(ioDispatcher)" inefficient due it's a direct call?
        paymentDao.getOrderSelections()
    }

    /**
     * Retrieves the image handle from internal storage.
     * Tries resolving with .png extension first; if not found or empty, falls back to .jpg.
     */
    override suspend fun getImageFile(fileName: String): Result<File> = runSuspendCatching {    /// Maybe it will be better to remove it later because the cleanest way to handle it is with the path in the repo
        withContext(ioDispatcher) {

            val readyRequest = requestDao.getLatestReadyRequest()
                ?: throw IllegalStateException("No READY request found")

            // Extracts base name regardless of whether 'fileName' contains an extension or not
            val baseName = "current_payment_image_${readyRequest.requestId}"

            // 1. Primary Attempt: Check for .png candidate
            val pngCandidate = File(context.filesDir, "$baseName.png")
            if (pngCandidate.isValidImageFile()) {
                return@withContext pngCandidate
            }

            // 2. Fallback Attempt: Check for .jpg candidate
            val jpgCandidate = File(context.filesDir, "$baseName.jpg")
            if (jpgCandidate.isValidImageFile()) {
                return@withContext jpgCandidate
            }

            throw FileNotFoundException(
                "Image file for '$fileName' (tried .png, .jpg) does not exist in filesDir."
            )
        }
    }
    /**
     * Internal extension for atomic file integrity check.
     */
    private fun File.isValidImageFile(): Boolean = exists() && length() > 0L

    // ... removeCurrenImageFromInternalStorage() ...
    // TODO

    override suspend fun savePaymentImageToInternalStorage(uri: Uri): Result<File> = withContext(ioDispatcher) {
        runCatching {
            val readyRequest = requestDao.getLatestReadyRequest()
                ?: throw IllegalStateException("No READY request found")

            // Generation handler
            val contentResolver = context.contentResolver

            // Determine file extension or default to jpg
            val mimeType = contentResolver.getType(uri)
            val extension = if (mimeType?.contains("png") == true) "png" else "jpg" // I guess it doesn't cover all extensions, but maybe it's ok because the phones' SS has these extension

            // Shaping output
            val targetFile = File(context.cacheDir, "current_payment_image_${readyRequest.requestId}.$extension")   // TODO: Change that random UUID by a timestamp, so we can use it later in the logic to get or delete it.

            // Byte Streaming and Copying
            contentResolver.openInputStream(uri)?.use { inputStream ->
                targetFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: throw IllegalStateException("Unable to open input stream for Uri: $uri")



            targetFile
        }
    }
}


//            // Create the entity using the file's absolute path   // TODO
//            val payment = PaymentEntity(
//                amountInCents = amountCents,
//                imagePath = targetFile.absolutePath //  "Houses/cache/payment_xyz.jpg"
//            )
//
//            // Save the text path to Room
//            paymentDao.insertPayment(payment)