package com.example.yummiziapp.features.payment.ui.arrival

import com.example.yummiziapp.features.payment.ui.model.ArrivalUiModel

sealed interface ArrivalUiState {
    data object Loading : ArrivalUiState
    data class Success(val arrivalInfo: ArrivalUiModel) : ArrivalUiState
    data class Error(val message: String) : ArrivalUiState
}