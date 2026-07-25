package com.example.yummiziapp.core.data.repository

import javax.inject.Inject
import android.content.Context
import android.net.Uri
import com.example.yummiziapp.core.data.di.IoDispatcher
import com.example.yummiziapp.features.payment.domain.repository.PaymentRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.uuid.Uuid

/// Should I put a singleton in every repository?
class PaymentRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PaymentRepository {

    override suspend fun savePaymentImageToInternalStorage(uri: Uri): Result<File> = withContext(ioDispatcher) {
        runCatching {
            // Generation handler
            val contentResolver = context.contentResolver

            // Determine file extension or default to jpg
            val mimeType = contentResolver.getType(uri)
            val extension = if (mimeType?.contains("png") == true) "png" else "jpg"

            // Shaping output
            val targetFile = File(context.cacheDir, "selected_image_${Uuid.random()}.$extension")

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