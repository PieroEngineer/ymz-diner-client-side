package com.example.yummiziapp.features.payment.ui.beforepay

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.core.utils.toCurrencyFormat
import com.example.yummiziapp.features.payment.domain.repository.PaymentRepository
import com.example.yummiziapp.features.restaurant.domain.repository.RestaurantRepository
import com.example.yummiziapp.features.payment.ui.model.PaymentPreparationUiModel
import com.example.yummiziapp.features.restaurant.ui.food.FoodSelectionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class PaymentPreparationViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val paymentRepository: PaymentRepository,
    savedStateHandle: SavedStateHandle
)  : ViewModel(){

    private val _pickedImageUiState = MutableStateFlow<PickedPaymentImageUiState>(PickedPaymentImageUiState.NoImageSelected)   // Saying that the image starts as not selected always when the screen is started
    val pickedImageUiState = _pickedImageUiState.asStateFlow()


    private val restaurantIdString: String = checkNotNull(savedStateHandle["restaurantId"])

    private val restaurantId = Uuid.parse(restaurantIdString)   /// Maybe it's not necessary here because there is only one reatuarant READY at the time


    init{   // TODO: Remove all the "init" and replace them with "onStart" in flows (Gemini "📱 Kotlin expert 3.0" has the way)
        viewModelScope.launch {
            fetchPaymentInfo()
        }
    }

    private suspend fun fetchPaymentInfo(){
        restaurantRepository.fetchPaymentInfo(restaurantId)
    }

    fun onPaymentSendClicked() {
        viewModelScope.launch {
            paymentRepository.uploadSelectedOrder()
        }
    }

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

    val paymentInfoUiState: StateFlow<PaymentPreparationUiState> = combine(
        restaurantRepository.observeRestaurantById(restaurantId),
        paymentRepository.observeCurrentOrder()
    ) { restaurant, orderSelections ->
        if (restaurant == null) {
            PaymentPreparationUiState.Error("Restaurant not found")
        } else {
            val totalCents = orderSelections.sumOf { it.quantity * it.historicalUnitPriceInCents }
            
            PaymentPreparationUiState.Success(
                PaymentPreparationUiModel(
                    restaurantLogoUrl = restaurant.logoUrl,
                    totalPayment = totalCents.toCurrencyFormat(),
                    paymentQrCodeUrl = restaurant.paymentQrUrl ?: "",
                    phoneNumber = restaurant.phoneNumberToPay.toString() ?: ""
                )
            )
        }
    }
    .catch { e -> emit(PaymentPreparationUiState.Error(e.message ?: "Unknown Error in payment stage")) }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PaymentPreparationUiState.Loading
    )

}