package com.yummizi.app.features.payment.ui.arrival

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yummizi.app.core.data.local.entities.Order
import com.yummizi.app.core.data.local.model.FeedbackLevelToken
import com.yummizi.app.core.utils.toCurrencyFormat
import com.yummizi.app.features.contact.domain.repository.ContactRepository
import com.yummizi.app.features.payment.domain.repository.PaymentRepository
import com.yummizi.app.features.payment.ui.model.ArrivalUiModel
import com.yummizi.app.features.payment.ui.model.FeedbackUiModel
import com.yummizi.app.features.request.domain.repository.RequestRepository
import com.yummizi.app.features.restaurant.domain.repository.RestaurantRepository
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
import kotlin.time.Clock
import kotlin.uuid.Uuid

@HiltViewModel
class ArrivalViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val restaurantRepository: RestaurantRepository,
    private val requestRepository: RequestRepository,
    private val contactRepository: ContactRepository,
    savedStateHandle: SavedStateHandle
)  : ViewModel() {

    private val restaurantIdString: String = checkNotNull(savedStateHandle["restaurantId"])

    private val restaurantId = Uuid.parse(restaurantIdString)

    private val _isTimeExpired = MutableStateFlow(false)

    private val _isSubmittingFeedback = MutableStateFlow(false)

    private val _feedbackUiModel = MutableStateFlow(FeedbackUiModel())
    val feedbackUiModel: StateFlow<FeedbackUiModel> = _feedbackUiModel.asStateFlow()

    private val _effect = Channel<ArrivalUiEvent>(Channel.BUFFERED)
    val navEffect = _effect.receiveAsFlow()

    fun onDescriptionChanged(newText: String) {
        _feedbackUiModel.update { currentState ->
            currentState.copy(description = newText)
        }
    }

    fun onConformityDegreeChanged(newDegree: FeedbackLevelToken) {
        _feedbackUiModel.update { currentState ->
            currentState.copy(conformityDegree = newDegree)
        }
    }

    fun goToFeedbackState(){
        _isTimeExpired.update{ !it }
    }

    fun onContactRestaurant(){
        viewModelScope.launch {
            val currentRestaurant = restaurantRepository.getRestaurantById(restaurantId)
            if (currentRestaurant != null){
                _effect.send(ArrivalUiEvent.IntentNavigateToLink(
                    "https://wa.me/${currentRestaurant.phoneNumberToContact}?text=Hola%2C%20vengo%20desde%20la%20aplicaci%C3%B3n%20de%20Yummizi%2C%20quisiera%20comentarle%20que%20"
                ))
            }
        }
    }

    fun onFindLocationClicked(){
        viewModelScope.launch {

            val currentRestaurant = restaurantRepository.getRestaurantById(restaurantId) ?: return@launch

            _effect.send(ArrivalUiEvent.IntentNavigateToLink(
                "https://www.google.com/maps/search/?api=1&query=${currentRestaurant.latitude},${currentRestaurant.longitude}"
            ))
        }
    }

    fun onFeedbackReady() { // TODO: Include a logic that omit the last feedback form if the already user gave feedback in the arrival time

        val currentFeedbackUiState = _feedbackUiModel.value

        viewModelScope.launch {

            contactRepository.sendFeedback(currentFeedbackUiState.conformityDegree, currentFeedbackUiState.description)

            _feedbackUiModel.update{ FeedbackUiModel() }

            if(arrivalInfoUiState.value !is ArrivalUiState.CustomerArrived) return@launch
            requestRepository.clearAll()    /// If the requests are removed here, doesn't it affect the above function that uses requestID?

            _effect.send(ArrivalUiEvent.NavigateToHome)

        }

    }

    fun onFeedbackSubmission() {
        _isSubmittingFeedback.update { true }
    }

    val arrivalInfoUiState: StateFlow<ArrivalUiState> = combine (

        paymentRepository.observeLatestOrder(),
        restaurantRepository.observeRestaurantById(restaurantId),

        _isTimeExpired,
        _isSubmittingFeedback

    ) { order, restaurant, isTimeExpired, isSubmittingFeedback ->
        if (!isSubmittingFeedback){
            val now = Clock.System.now()
            if(isTimeExpired || (order.expectedArrivalDatetime != null && order.expectedArrivalDatetime < now)){

                val message = if (restaurant != null) "en ${restaurant.name}" else "anterior"
                ArrivalUiState.CustomerArrived( message)
            }
            else{
                ArrivalUiState.CustomerArriving( order.toUiModel(restaurant?.logoUrl ?: "" /* TODO: Modify this fallback */) ) as ArrivalUiState
            }
        }
        else{
            ArrivalUiState.Loading
        }


    }
    .catch { e -> emit(ArrivalUiState.Error(e.message ?: "Unknown Error")) }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ArrivalUiState.Loading
    )

    private fun Order.toUiModel(restaurantLogoUrl: String) = ArrivalUiModel(
        restaurantLogoUrl = restaurantLogoUrl,
        paymentImagePath = checkNotNull(paymentImageRelativePath) { "e1" },
        totalPayment = checkNotNull(totalPriceInCentsFromBackend) { "e2" }.toCurrencyFormat(),
        waitBeginsDatetime = checkNotNull(localOrderCreationTimestamp) { "e3" },
        waitEndsDatetime = checkNotNull(expectedArrivalDatetime) { "e4" },
    )


}