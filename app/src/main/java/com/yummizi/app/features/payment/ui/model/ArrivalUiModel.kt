package com.yummizi.app.features.payment.ui.model

import java.io.File
import kotlin.time.Instant

data class ArrivalUiModel(
    val restaurantLogoUrl: String,
    // val restaurantName: String //TODO
    val paymentImagePath: String,
    val totalPayment: String,
    val waitBeginsDatetime: Instant,
    val waitEndsDatetime: Instant,
)
