package com.example.yummiziapp.features.payment.domain.repository

import android.net.Uri
import com.example.yummiziapp.core.data.local.entities.OrderSelection
import kotlinx.coroutines.flow.Flow
import java.io.File
import kotlin.uuid.Uuid

interface PaymentRepository {
    suspend fun savePaymentImageToInternalStorage(uri: Uri): Result<File>

    suspend fun selectOrder(orderMap: Map<Uuid, Int>)

    fun observeCurrentOrder(): Flow<List<OrderSelection>>

    suspend fun getCurrentOrder(): List<OrderSelection>

    suspend fun confirmSelectedOrder()
}
