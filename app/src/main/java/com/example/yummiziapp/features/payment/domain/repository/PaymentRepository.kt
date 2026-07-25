package com.example.yummiziapp.features.payment.domain.repository

import android.net.Uri
import java.io.File

interface PaymentRepository {
    suspend fun savePaymentImageToInternalStorage(uri: Uri): Result<File>
}