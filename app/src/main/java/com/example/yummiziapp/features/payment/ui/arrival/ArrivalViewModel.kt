package com.example.yummiziapp.features.payment.ui.arrival

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.core.data.local.entities.Order
import com.example.yummiziapp.core.utils.toCurrencyFormat
import com.example.yummiziapp.features.payment.domain.repository.PaymentRepository
import com.example.yummiziapp.features.payment.ui.model.ArrivalUiModel
import com.example.yummiziapp.features.restaurant.domain.repository.RestaurantRepository
import com.example.yummiziapp.features.user.ui.specification.UserSpecificationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class ArrivalViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val restaurantRepository: RestaurantRepository,
    savedStateHandle: SavedStateHandle
)  : ViewModel() {

    private val restaurantIdString: String = checkNotNull(savedStateHandle["restaurantId"])

    private val restaurantId = Uuid.parse(restaurantIdString)

    val arrivalInfoUiState: StateFlow<ArrivalUiState> = combine (
        paymentRepository.observeLatestOrder(),
        restaurantRepository.observeRestaurantById(restaurantId),
    ) { order, restaurant ->

            ArrivalUiState.Success( order.toUiModel(restaurant?.logoUrl ?: "" /* TODO: Modify this fallback */) ) as ArrivalUiState
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