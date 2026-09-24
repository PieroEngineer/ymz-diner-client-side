package com.yummizi.app.features.payment.ui.checkin

import java.io.File

sealed interface PickedPaymentImageUiState {
    data object NoImageSelected : PickedPaymentImageUiState
    data object LoadingImageSelected : PickedPaymentImageUiState
    data class ImageAlreadySelected ( val selectedFile: File) : PickedPaymentImageUiState   // TODO (#945): Change the "File" type for the "Uri" type (see in "📱 Kotlin expert 5.0" for "Why Uri.fromFile(file) was NOT used")
}