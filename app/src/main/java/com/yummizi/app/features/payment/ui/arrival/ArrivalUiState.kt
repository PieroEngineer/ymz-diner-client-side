package com.yummizi.app.features.payment.ui.arrival

import com.yummizi.app.features.payment.ui.model.ArrivalUiModel

sealed interface ArrivalUiState {
    data object Loading : ArrivalUiState
    data class CustomerArriving(val arrivalInfo: ArrivalUiModel) : ArrivalUiState
    data class CustomerArrived(val topMessagePart: String) : ArrivalUiState
    data class Error(val message: String) : ArrivalUiState
}