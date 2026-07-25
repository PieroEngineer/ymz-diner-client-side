package com.example.yummiziapp.features.payment.ui.model

data class PaymentPreparationUiModel(
    val restaurantLogoUrl: String,
    val totalPayment: String,
    val paymentQrCodeUrl: String,
    val phoneNumber: Int,
)
