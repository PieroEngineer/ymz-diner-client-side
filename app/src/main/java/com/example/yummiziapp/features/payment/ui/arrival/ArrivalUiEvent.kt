package com.example.yummiziapp.features.payment.ui.arrival

sealed interface ArrivalUiEvent {
    data object OnToggleByTimeExpired : ArrivalUiEvent
    data object NavigateToHome: ArrivalUiEvent
}