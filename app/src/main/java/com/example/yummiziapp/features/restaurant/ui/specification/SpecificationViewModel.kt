package com.example.yummiziapp.features.restaurant.ui.specification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.core.data.local.entities.Restaurant
import com.example.yummiziapp.features.request.data.model.QueueStatus
import com.example.yummiziapp.features.request.domain.repository.RequestRepository
import com.example.yummiziapp.features.restaurant.domain.repository.RestaurantRepository
import com.example.yummiziapp.features.restaurant.ui.model.SelectableUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class SpecificationViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val requestRepository: RequestRepository
) : ViewModel() {

    // Extension function for clean mapping
    // Updated Mapper
    private fun Restaurant.toUiModel(status: QueueStatus) = SelectableUiModel(
        restaurantId = id,
        distanceKm = 10,
        restaurantLogoUrl = logoUrl,
        waitingState = status // Dynamic status from the DB!
    )

    val queuedRestaurants: StateFlow<List<SelectableUiModel>> = restaurantRepository
        .observePendingRestaurantsWithStatus() // Now returning Flow<List<RestaurantWithStatus>>
        .map { list ->
            list.map { item ->
                // Use an extension or helper to convert
                item.restaurant.toUiModel(item.status)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    private val _effect = Channel<SpecificationUiEvents>(Channel.BUFFERED)
    val navEffect = _effect.receiveAsFlow()

    fun onRestaurantClicked(id: Uuid) {
//        Log.d("MainActivity", "🔎  Starting coroutine with this ID: $id")
        viewModelScope.launch {
            // "I've decided we need to move to the next screen"
            _effect.send(SpecificationUiEvents.NavigateToRestaurantMenu(id))
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