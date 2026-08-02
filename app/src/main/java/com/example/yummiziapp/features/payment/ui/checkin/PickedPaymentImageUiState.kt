package com.example.yummiziapp.features.payment.ui.checkin

import java.io.File

sealed interface PickedPaymentImageUiState {
    data object NoImageSelected : PickedPaymentImageUiState
    data object LoadingImageSelected : PickedPaymentImageUiState
    data class ImageAlreadySelected ( val selectedFile: File) : PickedPaymentImageUiState
}