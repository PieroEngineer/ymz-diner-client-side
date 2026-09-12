package com.example.yummiziapp.features.payment.ui.checkin

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.core.utils.toCurrencyFormat
import com.example.yummiziapp.features.payment.domain.repository.PaymentRepository
import com.example.yummiziapp.features.restaurant.domain.repository.RestaurantRepository
import com.example.yummiziapp.features.payment.ui.model.PaymentPreparationUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
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
)  : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _pickedImageUiState = MutableStateFlow<PickedPaymentImageUiState>(PickedPaymentImageUiState.NoImageSelected)   // Saying that the image starts as not selected always when the screen is started
    val pickedImageUiState = _pickedImageUiState.asStateFlow() /// Ask if it's a bad practice to use this

    private val restaurantIdString: String = checkNotNull(savedStateHandle["restaurantId"])
    private val restaurantId = Uuid.parse(restaurantIdString)   /// Maybe it's not necessary here because there is only one reatuarant READY at the time


    private val _effect = Channel<PaymentPreparationUiEvents>(Channel.BUFFERED)
    val navEffect = _effect.receiveAsFlow()

    init{   // TODO: Remove all the "init" and replace them with "onStart" in flows (Gemini "📱 Kotlin expert 3.0" has the way)
        viewModelScope.launch {
            fetchPaymentInfo()
        }
    }
    private suspend fun fetchPaymentInfo(){
        restaurantRepository.fetchPaymentInfo(restaurantId)
    }

    fun onSendPaymentClicked() {
        viewModelScope.launch {
            val currentPickedImageUiState = _pickedImageUiState.value
            if (currentPickedImageUiState is PickedPaymentImageUiState.ImageAlreadySelected) {
                _isLoading.update { true }
                paymentRepository.confirmSelectedOrder(currentPickedImageUiState.selectedFile.name, Uri.fromFile(currentPickedImageUiState.selectedFile))   // TODO (#945)
                    .onSuccess {
                        Log.d("MyActivity", "✅ ViewModel received a success response of the S3 upload")
                        _effect.send(PaymentPreparationUiEvents.NavigateToArrival(restaurantId))
                    }
                    .onFailure {
                        Log.d("MyActivity", "🚩 ViewModel received a failure response of the S3 upload")
                        _effect.send(PaymentPreparationUiEvents.ShowSnackbar("Hubo un problema al enviar la imagen, por favor, intente de nuevo\nSi ya pagó, puede acercarse al restaurante."))
                        _isLoading.update { false }
                    }
            }
            else {
                _effect.send(PaymentPreparationUiEvents.ShowSnackbar("Por favor, seleccione una imagen"))
            }
        }
    }

    fun onCopyPhoneNumber(){
        viewModelScope.launch {
            val currentPaymentInfoUi = paymentInfoUiState.value
            if (currentPaymentInfoUi is PaymentPreparationUiState.CheckIn) {
                _effect.send(PaymentPreparationUiEvents.CopyToClipboard(
                    copiedText = currentPaymentInfoUi.restaurantInfo.phoneNumberToPay,
                    accompanyingText ="✅  El número del restaurante\nfue copiado"
                ))
            }
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
                    // TODO: SnackBar
                }
        }
    }

    val paymentInfoUiState: StateFlow<PaymentPreparationUiState> = combine(
        restaurantRepository.observeRestaurantById(restaurantId),
        paymentRepository.observeCurrentOrderSelections(),    // TODO: Should I change it for an observer that only observes the total cost?
        _pickedImageUiState,
        _isLoading
    ) { restaurant, orderSelections, pickedImageUiState, isLoading ->

        if(isLoading){
            return@combine PaymentPreparationUiState.Loading
        }
        else{
            val imageReady = pickedImageUiState is PickedPaymentImageUiState.ImageAlreadySelected

            if (restaurant == null) {
                return@combine PaymentPreparationUiState.Error("Restaurant not found")
            } else {
                val totalCents = orderSelections.sumOf { it.quantity * it.historicalUnitPriceInCents }  // TODO: Consider that this price should be from the backend and not from local
                return@combine PaymentPreparationUiState.CheckIn(
                    PaymentPreparationUiModel(
                        restaurantLogoUrl = restaurant.logoUrl,
                        totalPayment = totalCents.toCurrencyFormat(),
                        paymentQrCodeUrl = restaurant.paymentQrUrl ?: "",
                        phoneNumberToPay = restaurant.phoneNumberToPay.toString() ?: "",
                        imageReady = imageReady
                    )
                )
            }
        }

    }
    .catch { e -> emit(PaymentPreparationUiState.Error(e.message ?: "Unknown Error in payment stage")) }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PaymentPreparationUiState.Loading
    )

}