package com.example.yummiziapp.features.payment.ui.beforepay

import java.io.File

sealed interface PaymentPreparationUiState{
    data object Loading : PaymentPreparationUiState
    data class Success(val selectedFile: File) : PaymentPreparationUiState
    data class Error(val message: String) : PaymentPreparationUiState
}
