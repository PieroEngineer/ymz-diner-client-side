package com.example.yummiziapp.features.payment.ui.beforepay

sealed interface PaymentPreparationUiEvents {
    data object NavigateToAfterPayment : PaymentPreparationUiEvents
}