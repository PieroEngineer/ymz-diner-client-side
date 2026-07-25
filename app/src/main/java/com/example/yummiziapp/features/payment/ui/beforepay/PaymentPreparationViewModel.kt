package com.example.yummiziapp.features.payment.ui.beforepay

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.features.payment.domain.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentPreparationViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
)  : ViewModel(){

    private val _pickedImageUiState = MutableStateFlow<PickedPaymentImageUiState>(PickedPaymentImageUiState.NoImageSelected)   // Saying that the image starts as not selected always when the screen is started
    val pickedImageUiState = _pickedImageUiState.asStateFlow()

    fun onImagePicked(uri: Uri?) {
        if (uri == null) return

        viewModelScope.launch {
            _pickedImageUiState.update { PickedPaymentImageUiState.LoadingImageSelected }

            paymentRepository.savePaymentImageToInternalStorage(uri)
                .onSuccess { persistentFile ->
                    _pickedImageUiState.update { currentState ->
                        PickedPaymentImageUiState.ImageAlreadySelected(selectedFile = persistentFile)
                    }
                }
                .onFailure { exception ->
                    _pickedImageUiState.update { PickedPaymentImageUiState.NoImageSelected }
                }
        }
    }

}