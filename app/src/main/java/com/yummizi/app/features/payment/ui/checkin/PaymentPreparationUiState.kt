package com.yummizi.app.features.payment.ui.checkin

import com.yummizi.app.features.payment.ui.model.PaymentPreparationUiModel

sealed interface PaymentPreparationUiState{
    data object Loading : PaymentPreparationUiState
    data class CheckIn(val restaurantInfo: PaymentPreparationUiModel) : PaymentPreparationUiState
    data class Error(val message: String) : PaymentPreparationUiState
}
