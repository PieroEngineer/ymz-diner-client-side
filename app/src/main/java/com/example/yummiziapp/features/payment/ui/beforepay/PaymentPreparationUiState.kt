package com.example.yummiziapp.features.payment.ui.beforepay

import com.example.yummiziapp.features.payment.ui.model.PaymentPreparationUiModel
import java.io.File

sealed interface PaymentPreparationUiState{
    data object Loading : PaymentPreparationUiState
    data class Success(val restaurantInfo: PaymentPreparationUiModel) : PaymentPreparationUiState
    data class Error(val message: String) : PaymentPreparationUiState
}
