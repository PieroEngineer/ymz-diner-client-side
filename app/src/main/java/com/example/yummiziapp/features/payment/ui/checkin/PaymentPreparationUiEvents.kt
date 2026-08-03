package com.example.yummiziapp.features.payment.ui.checkin

import kotlin.uuid.Uuid

sealed interface PaymentPreparationUiEvents {
    data class NavigateToArrival(val restaurantId: Uuid): PaymentPreparationUiEvents
}