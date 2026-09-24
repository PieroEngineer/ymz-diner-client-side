package com.yummizi.app.features.payment.domain.repository

import android.net.Uri
import com.yummizi.app.core.data.local.entities.Order
import com.yummizi.app.core.data.local.entities.OrderSelection
import kotlinx.coroutines.flow.Flow
import java.io.File
import kotlin.uuid.Uuid

interface PaymentRepository {
    suspend fun savePaymentImageToInternalStorage(uri: Uri): Result<File>
    suspend fun selectOrder(orderMap: Map<Uuid, Int>)
    suspend fun getCurrentOrderSelections(): List<OrderSelection>

    suspend fun getImageFile(): Result<File>

    fun observeLatestOrder(): Flow<Order>

    fun observeCurrentOrderSelections(): Flow<List<OrderSelection>>

    suspend fun confirmSelectedOrder(paymentImageRelativePath: String, imageUri: Uri?): Result<Unit>
}
