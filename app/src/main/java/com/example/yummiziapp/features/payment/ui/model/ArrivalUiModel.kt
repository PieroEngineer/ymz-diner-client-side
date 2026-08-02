package com.example.yummiziapp.features.payment.ui.model

import kotlin.time.Instant

data class ArrivalUiModel(
    val restaurantLogoUrl: String,
    // val restaurantName: String //TODO
    val totalPayment: String,
    val WaitBeginsDatetime: Instant,
    val WaitEndsDatetime: Instant,
)
