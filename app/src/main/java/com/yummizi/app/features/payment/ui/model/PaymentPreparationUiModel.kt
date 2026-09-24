package com.yummizi.app.features.payment.ui.model

data class PaymentPreparationUiModel(
    val restaurantLogoUrl: String,
    // val restaurantName: String   // TODO
    val totalPayment: String,
    val paymentQrCodeUrl: String,
    val phoneNumberToPay: String,
    val imageReady: Boolean
)
