package com.example.yummiziapp.features.payment.ui.checkin

import kotlin.uuid.Uuid

sealed interface PaymentPreparationUiEvents {
    data class NavigateToArrival(val restaurantId: Uuid): PaymentPreparationUiEvents
    data class CopyToClipboard(val copiedText: String, val accompanyingText: String): PaymentPreparationUiEvents
}