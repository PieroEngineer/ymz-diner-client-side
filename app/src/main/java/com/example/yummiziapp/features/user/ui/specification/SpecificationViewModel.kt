package com.example.yummiziapp.features.user.ui.specification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.core.data.local.entities.Restaurant
import com.example.yummiziapp.core.data.local.model.RequestStatusToken
import com.example.yummiziapp.features.request.domain.repository.RequestRepository
import com.example.yummiziapp.features.restaurant.domain.repository.RestaurantRepository
import com.example.yummiziapp.features.restaurant.ui.model.SelectedRestaurantUiModel
import com.example.yummiziapp.features.user.ui.model.FocusedRestaurantUiModel
import com.example.yummiziapp.features.user.ui.model.RequestUiStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class SpecificationViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val requestRepository: RequestRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val restaurantIdString: String? = savedStateHandle["restaurantId"]
    private val restaurantIdReceivedFromNavigation = restaurantIdString?.let { Uuid.parseOrNull(it) }

    // The Reactive Engine Core: Wrap the ID inside a StateFlow
    private val _focusedRestaurantId = MutableStateFlow<Uuid?>(restaurantIdReceivedFromNavigation)
    val focusedRestaurantId: StateFlow<Uuid?> = _focusedRestaurantId.asStateFlow()

    // 🔄 2. The Main UI State Machine Pipeline
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<SpecificationUiState> = _focusedRestaurantId
        .flatMapLatest { restaurantId ->
            // Whenever _focusedRestaurantId updates its value, this block re-runs automatically!
            if (restaurantId != null) {
                Log.d("YummiziVM", "🔎👉 Switching state mode to FOCUSED: $restaurantId")

                restaurantRepository.observePendingRestaurantWithStatusById(restaurantId)
                    .map { currentRestaurant ->
                        if (currentRestaurant == null) {
                            SpecificationUiState.Error("Restaurant data allocation corrupted or missing.")
                        } else {
                            SpecificationUiState.FocusingRestaurant(
                                currentRestaurant.restaurant.toFocusedRestaurantUiModel(
                                    currentRestaurant.requestStatus.toUiStatus()
                                )
                            )
                        }
                    }
            } else {
                Log.d("YummiziVM", "🔎👉 Switching state mode to LISTING")

                restaurantRepository.observePendingRestaurantsWithStatus()
                    .map { currentSelectedRestaurants ->
                        SpecificationUiState.ListingRestaurants(
                            currentSelectedRestaurants.map {
                                it.restaurant.toSelectableRestaurantUiModel(
                                    it.requestStatus.toUiStatus()
                                )
                            }
                        )
                    }
            }
        }
        .catch { error ->
            Log.e("YummiziVM", "🚩 Operational pipeline failure", error)
            emit(SpecificationUiState.Error(error.message ?: "An unexpected layout error occurred."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SpecificationUiState.Loading
        )

    private fun Restaurant.toFocusedRestaurantUiModel(status: RequestUiStatus) = FocusedRestaurantUiModel(
        restaurantId = id,
        restaurantLogoUrl = logoUrl,
        waitingState = status,
        distanceKm = 10,
        name = name,
        maxWaitingTimeInMinutes = 15
    )

    // Extension function for clean mapping
    // Updated Mapper
    private fun Restaurant.toSelectableRestaurantUiModel(status: RequestUiStatus) = SelectedRestaurantUiModel(
        restaurantId = id,
        distanceKm = 10,
        restaurantLogoUrl = logoUrl,
        waitingState = status // Dynamic status from the DB!
    )

    // 🚀 High-Performance Pattern Matrix: Extension Mapper
    private fun RequestStatusToken.toUiStatus(): RequestUiStatus = when (this) {
        RequestStatusToken.IDLE -> RequestUiStatus.Idle
        RequestStatusToken.QUEUED -> RequestUiStatus.Queued
        RequestStatusToken.ACCEPTED -> RequestUiStatus.Accepted
        RequestStatusToken.PAID -> RequestUiStatus.Paid
        RequestStatusToken.DECLINED -> RequestUiStatus.Declined
    }


    private val _effect = Channel<SpecificationUiEvents>(Channel.BUFFERED)
    val navEffect = _effect.receiveAsFlow()


    // fun onGoBackClicked() {  // TODO
    //      /// Unfocus the state of the focused restaurant
    // }


    /// If the restaurant is still in "QUEUED", go to the focused restaurant details (arrival control)
    /// If the restaurant accepted the table request, open the confirmation pop-up
    /// If the restaurant is "PAID", this "onRestaurantClicked" doesn't have any sense because the app
    //  will show the focused restaurant directly with the confirmed arrival details

    fun onQueuedRestaurantClicked(id: Uuid) {
        _focusedRestaurantId.value = id
    }

    fun onAcceptedRestaurantClicked(id: Uuid) {


        _focusedRestaurantId.value = id
    }

    fun onDeclinedRestaurantClicked(id: Uuid) {
        _focusedRestaurantId.value = id
    }

    fun onQueueJoiningClicked(arrivalTimeInMinutes: Int, restaurantId: Uuid) {  //TODO: Later include here their company and specifics

        Log.d("MyActivity", "✅👥  Request made for $restaurantId with $arrivalTimeInMinutes minutes")

        viewModelScope.launch {
            requestRepository.createQueueRequest(restaurantId)
            _effect.send(SpecificationUiEvents.NavigateToHome)
        }
    }


    fun onHomeClicked() {
        viewModelScope.launch {
            _effect.send(SpecificationUiEvents.NavigateToHome)

        }
    }

    fun onYouClicked() {
        viewModelScope.launch {
            _effect.send(SpecificationUiEvents.NavigateToPreferences)
        }
    }

    fun leaveQueue(restaurantId: Uuid) {

        viewModelScope.launch {
            try {
                requestRepository.leaveQueue(restaurantId)
                //TODO: _effect.send(HomeScreenUiEvents.ShowSnackbar("You left the queue"))
            } catch (e: Exception) {
                // This is where you tell the user the "Optimistic" part failed
                Log.d("MyActivity", "🚩🚮 There was an error while trying to leave the queue  | $e\n")
                //_effect.send(HomeScreenUiEvents.ShowError("Check your connection. Could not leave queue."))

            }
        }
    }
}

// TODO: Replace queuedRestaurants' logic for this one that has the state
// 🛡️ Pro-Tip: Transform your incoming data into your unified UI State object
//    val uiState: StateFlow<SpecificationUiState> = restaurantRepository
//        .observePendingRestaurantsWithStatus()
//        .map { list ->
//            val uiModels = list.map { item -> item.restaurant.toUiModel(item.status) }
//            SpecificationUiState(
//                isLoading = false,
//                queuedRestaurants = uiModels,
//                errorMessage = null
//            )
//        }
//        .catch { error ->
//            emit(SpecificationUiState(isLoading = false, errorMessage = error.message))
//        }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = SpecificationUiState(isLoading = true)
//        )

//TODO: for the distance calculation
//private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
//    val r = 6371 // Radius of the earth in km
//    val dLat = Math.toRadians(lat2 - lat1)
//    val dLon = Math.toRadians(lon2 - lon1)
//    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
//            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//            Math.sin(dLon / 2) * Math.sin(dLon / 2)
//    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
//    return (r * c).toInt()
//}