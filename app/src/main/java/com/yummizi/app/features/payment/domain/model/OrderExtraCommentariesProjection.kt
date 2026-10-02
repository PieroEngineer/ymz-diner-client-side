package com.yummizi.app.features.payment.domain.model

data class OrderExtraCommentariesProjection(
    val orderId: String,
    val extraCommentaries: String?
)
