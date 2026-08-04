package com.example.yummiziapp.features.payment.ui.arrival

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.core.data.local.entities.Order
import com.example.yummiziapp.core.data.local.model.FeedbackLevelToken
import com.example.yummiziapp.core.utils.toCurrencyFormat
import com.example.yummiziapp.features.feedback.domain.repository.FeedbackRepository
import com.example.yummiziapp.features.payment.domain.repository.PaymentRepository
import com.example.yummiziapp.features.payment.ui.model.ArrivalUiModel
import com.example.yummiziapp.features.payment.ui.model.FeedbackUiState
import com.example.yummiziapp.features.request.domain.repository.RequestRepository
import com.example.yummiziapp.features.restaurant.domain.repository.RestaurantRepository
import com.example.yummiziapp.features.user.ui.specification.SpecificationUiEvents
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
    private val feedbackRepository: FeedbackRepository,
    savedStateHandle: SavedStateHandle
)  : ViewModel() {

    private val restaurantIdString: String = checkNotNull(savedStateHandle["restaurantId"])

    private val restaurantId = Uuid.parse(restaurantIdString)

    private val _isTimeExpired = MutableStateFlow(false)

    private val _isSubmittingFeedback = MutableStateFlow(false)

    private val _feedbackUiState = MutableStateFlow(FeedbackUiState())
    val feedbackUiState: StateFlow<FeedbackUiState> = _feedbackUiState.asStateFlow()

    private val _effect = Channel<ArrivalUiEvent>(Channel.BUFFERED)
    val navEffect = _effect.receiveAsFlow()

    fun onDescriptionChanged(newText: String) {
        _feedbackUiState.update { currentState ->
            currentState.copy(description = newText)
        }
    }

    fun onConformityDegreeChanged(newDegree: FeedbackLevelToken) {
        _feedbackUiState.update { currentState ->
            currentState.copy(conformityDegree = newDegree)
        }
    }

    fun goToFeedbackState(event: ArrivalUiEvent){
        when (event) {
            is ArrivalUiEvent.OnToggleByTimeExpired -> { _isTimeExpired.update{ !it } }
            else -> {}
        }
    }

    fun onFeedbackReady() {

        val currentFeedbackUiState = _feedbackUiState.value

        viewModelScope.launch {
        _isSubmittingFeedback.update { true }
            feedbackRepository.sendFinalExperienceFeedback(currentFeedbackUiState.conformityDegree, currentFeedbackUiState.description)
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