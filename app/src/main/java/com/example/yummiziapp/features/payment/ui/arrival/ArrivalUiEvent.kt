package com.example.yummiziapp.features.payment.ui.arrival

sealed interface ArrivalUiEvent {
    data object NavigateToHome: ArrivalUiEvent
    data class IntentContactRestaurant( val url: String): ArrivalUiEvent
}