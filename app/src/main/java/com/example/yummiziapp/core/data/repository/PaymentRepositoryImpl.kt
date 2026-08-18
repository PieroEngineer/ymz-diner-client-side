package com.example.yummiziapp.core.data.repository

import javax.inject.Inject
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.yummiziapp.core.data.di.IoDispatcher
import com.example.yummiziapp.core.data.local.dao.LoggedUserDao
import com.example.yummiziapp.core.data.local.dao.MenuDao
import com.example.yummiziapp.core.data.local.dao.OrderDao
import com.example.yummiziapp.core.data.local.dao.PaymentDao
import com.example.yummiziapp.core.data.local.dao.RequestDao
import com.example.yummiziapp.core.data.local.entities.Order
import com.example.yummiziapp.core.data.local.entities.OrderSelection
import com.example.yummiziapp.core.data.local.model.OrderWithSelectionsRelation
import com.example.yummiziapp.core.data.local.model.RequestStatusToken
import com.example.yummiziapp.core.data.network.api.PaymentApi
import com.example.yummiziapp.core.data.network.model.mappers.createSendOrderDto
import com.example.yummiziapp.core.data.network.model.mappers.toDtoList
import com.example.yummiziapp.features.payment.domain.repository.PaymentRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.File
import java.io.FileNotFoundException
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

/// Should I put a singleton in every repository?
class PaymentRepositoryImpl @Inject constructor(    // TODO: Change the name of "Payment" for another (Perhaps with "Order")
    private val paymentDao: PaymentDao,
    private val requestDao: RequestDao,
    private val menuDao: MenuDao,
    private val loggedUserDao: LoggedUserDao,
    private val orderDao: OrderDao,

    private val yummiziApi: PaymentApi,

    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PaymentRepository {

    override suspend fun selectOrder(orderMap: Map<Uuid, Int>) = withContext(ioDispatcher) {
//        runCatching { /// Should I use runCatching?
            // Get the current "READY" request and extract its ID and the Restaurant ID
        val readyRequest = requestDao.getLatestReadyRequest()
            ?: throw IllegalStateException("No READY request found when selecting an order")

        // Get dish details to get historical price
        val dishIds = orderMap.keys.toList()
        val dishes = menuDao.getDishesByIds(dishIds)
        val dishPriceMap = dishes.associateBy({ it.id }, { it.priceCents }) // TODO: Optimze this double association
        val dishNameMap = dishes.associateBy({ it.id }, { it.name })

        val newBaseOrder = Order(   // TODO: Move it above and create a mapper for it
            orderId = readyRequest.requestId,   /// (#294) Is it ok that I use the request ID here?
            requestId = readyRequest.requestId,
//            totalPriceInCentsFromBackend = totalPriceInCentsFromRemote, //TODO: Bring this value from remote
        )

        // Convert the map into a list of "OrderSelection" entities
        val orderSelections = orderMap.map { (dishId, quantity) ->  // TODO: Move it into a mapper in another file
            OrderSelection(
                selectionId = Uuid.random(),
                orderId = readyRequest.requestId,   /// (#294)
                dishId = dishId,
                dishName = dishNameMap[dishId] ?: "unnamed dish",
                quantity = quantity,
                historicalUnitPriceInCents = dishPriceMap[dishId] ?: 0
            )
        }

        // TODO: Include these DAO actions as a transacton

        // Save locally
        orderDao.insertOrderWithSelections(
            OrderWithSelectionsRelation(
                newBaseOrder,
                orderSelections
            )
        )
//        }
    }

    override suspend fun confirmSelectedOrder(paymentImageRelativePath: String, imageUri: Uri?) = withContext(ioDispatcher) {
        // Get the current "READY" request and extract its ID and the Restaurant ID
        val readyRequest = requestDao.getLatestReadyRequest()
            ?: throw IllegalStateException("No READY request found when confirming an order")

        val loggedUser = loggedUserDao.getUser() ?: throw IllegalStateException("No logged user found")

        val orderSelection = paymentDao.getOrderSelections()
        // Convert the list of "OrderSelection" into a list of "OrderSelectionDto"
        val orderSelectionDtos = orderSelection.toDtoList()

        // Sum of prices
        val totalPriceInCentsFromLocal = orderSelectionDtos.sumOf { it.historicalUnitPriceInCents * it.quantity }

        // Add the list of "OrderSelectionDto" into a "sendOrderReadyToBePaidDto"
        val payload = createSendOrderDto(   // TODO: change it to a function that only receives the request and logged user directly to make it looks better
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

        if (response.isSuccessful) {    // TODO: Also return the time when the customer should arrive, the client-side doesn't decide that  /// perhaps also the time when the waiting started


            requestDao.updateRequestStatus(readyRequest.requestId, RequestStatusToken.PAID)

            // Receive the response with the total price and return it
            val totalPriceInCentsFromRemote = response.body()?.totalPriceInCents
                ?: throw IllegalStateException("Empty response body")

            val officialOrder = Order(   // TODO: Move it above and create a mapper for it
                orderId = readyRequest.requestId,   /// Is it ok that I use the request ID here?
                paymentImageRelativePath = paymentImageRelativePath,
                expectedArrivalDatetime = Clock.System.now() + readyRequest.arrivalTimeInMinutes.minutes,
                requestId = readyRequest.requestId,
                localOrderCreationTimestamp = Clock.System.now(),
                totalPriceInCentsFromBackend = totalPriceInCentsFromRemote,
            )

            orderDao.updateOrder(officialOrder)

            if (totalPriceInCentsFromLocal != totalPriceInCentsFromRemote) {
                Log.d("MyActivity", "\n🚩💲  The local price doesn't match with the remote price:\n  Local: $totalPriceInCentsFromLocal\n  Remote: $totalPriceInCentsFromRemote")
            }
            else{
                Log.d("MyActivity", "✅💲  The local price matches with the remote price")
            }

            val presignedUrl = response.body()?.paymentImageReferences?.uploadUrl//.replace('172.18.0.2', 'localhost') // FIXME: If we use the "repalce" function, remove it in production
            val fileKey = response.body()?.paymentImageReferences?.publicUrl    // TODO: Pass to the Room and use it in the next screen

            Log.d("MyActivity", "🔎⏫  Presigned URL: $presignedUrl")

            try{
                if (imageUri != null && presignedUrl != null){
                    uploadImage(imageUri, presignedUrl)
                    Log.d("MyActivity", "✅⏫  Image uploaded successfully")

                    Unit
                }
                else{
                    throw Exception("🚩  Image URI or Presigned URL is null")
                }
            }
            catch (e: Exception){
                // TODO: Let know to the restaurant that the image payment wasn't successfully sent (But depend in what part of the Lambda the error occured)
                throw Exception("🚩  Image Uploading to S3 failed: ${e.message}")
            }

        } else {
            throw Exception("API call failed: ${response.code()} ${response.message()}")
        }
    }
    private suspend fun uploadImage(uri: Uri, presignedUrl: String) {
        runCatching {
            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"

            // 2. Efficiently stream image from Uri without loading whole file in RAM
            val requestBody = object : RequestBody() {
                override fun contentType() = mimeType.toMediaTypeOrNull()

                override fun writeTo(sink: BufferedSink) {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        sink.writeAll(inputStream.source())
                    } ?: throw IllegalStateException("🚩  Could not open InputStream for Uri")
                }
            }

            // 3. Upload directly to S3
            val uploadResponse = yummiziApi.uploadToPresignedUrl(presignedUrl, mimeType, requestBody)
            if (!uploadResponse.isSuccessful) {
                throw IllegalStateException("S3 Upload failed with code: ${uploadResponse.code()}")
            }
        }
    }

    override fun observeLatestOrder(): Flow<Order> = orderDao.observeLatestOrder()

    override fun observeCurrentOrderSelections(): Flow<List<OrderSelection>> = paymentDao.observeOrderSelections()

    override suspend fun getCurrentOrderSelections(): List<OrderSelection> = paymentDao.getOrderSelections()

    /**
     * Retrieves the image handle from internal storage.
     * Tries resolving with .png extension first; if not found or empty, falls back to .jpg.
     */
    override suspend fun getImageFile(): Result<File> = runCatching {    /// Maybe it will be better to remove it later because the cleanest way to handle it is with the path in the repo
        withContext(ioDispatcher) {

            val readyRequest = requestDao.getLatestReadyRequest()
                ?: throw IllegalStateException("No READY request found when retrieving image")

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
                "Image file for '$baseName' (tried .png, .jpg) does not exist in filesDir."
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
                ?: throw IllegalStateException("No READY request found when saving image")

            // Generation handler
            val contentResolver = context.contentResolver

            // Determine file extension or default to jpg
            val mimeType = contentResolver.getType(uri)
            val extension = if (mimeType?.contains("png") == true) "png" else "jpg" // I guess it doesn't cover all extensions, but maybe it's ok because the phones' SS has these extension

            // Shaping output
            val targetFile = File(context.filesDir/* *Or cacheDir */, "current_payment_image_${readyRequest.requestId}.$extension")   // TODO: Change that random UUID by a timestamp, so we can use it later in the logic to get or delete it.

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