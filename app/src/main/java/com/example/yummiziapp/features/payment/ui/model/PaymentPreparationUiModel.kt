package com.example.yummiziapp.features.payment.ui.model

import java.io.File

data class PaymentPreparationUiModel(
    val restaurantLogoUrl: String,
    // val restaurantName: String   // TODO
    val totalPayment: String,
    val paymentQrCodeUrl: String,
    val phoneNumber: String,
    val imageReady: Boolean
)
