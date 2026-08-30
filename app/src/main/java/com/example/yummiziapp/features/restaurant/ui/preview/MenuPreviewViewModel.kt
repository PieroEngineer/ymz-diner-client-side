package com.example.yummiziapp.features.restaurant.ui.preview

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.yummiziapp.core.data.local.model.RequestStatusToken
import com.example.yummiziapp.features.request.domain.repository.RequestRepository
import com.example.yummiziapp.features.restaurant.domain.repository.RestaurantRepository
import com.example.yummiziapp.features.restaurant.ui.model.RestaurantInfoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map
import kotlin.uuid.Uuid

@HiltViewModel
class MenuPreviewViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val requestRepository: RequestRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _effect = Channel<MenuPreviewUiEvents>(Channel.BUFFERED)
    val navEffect = _effect.receiveAsFlow()

    // Get the "restaurantId" from the Navigation Route string
    private val restaurantIdString: String = checkNotNull(savedStateHandle["restaurantId"])  // For one restaurant got

    private val restaurantId = Uuid.parse(restaurantIdString)

    init {
        // Trigger the GET request to cloud immediately on start
        refreshMenu()
    }

    fun onAddRestaurantClicked() {
        viewModelScope.launch {

            val currentRestaurantState = requestRepository.getRequestStatusByRestaurantId(restaurantId)

            when (currentRestaurantState) {
                RequestStatusToken.READY, RequestStatusToken.PAY_TRIED -> {
                    _effect.send(MenuPreviewUiEvents.NavigateToReadyRestaurantMenu(restaurantId))
                }
                RequestStatusToken.PAID -> {
                    _effect.send(MenuPreviewUiEvents.NavigateToPaidRestaurant(restaurantId))
                }
                else -> {
                    Log.d("MyActivity", "🔎  From the Menu Preview ViewModel, the Current restaurant ID is $restaurantId whose status is ${ currentRestaurantState.name}")
                    _effect.send(MenuPreviewUiEvents.NavigateToFocusedRestaurant(restaurantId))
                }
            }
        }
    }

    fun onFindLocationClicked(){
        viewModelScope.launch {

            val currentRestaurant =
                restaurantRepository.getRestaurantById(restaurantId) ?: return@launch

            _effect.send(MenuPreviewUiEvents.IntentFindLocation(
                "https://www.google.com/maps/search/?api=1&query=${currentRestaurant.latitude},${currentRestaurant.longitude}"
            ))
        }
    }


    private fun refreshMenu() {
        viewModelScope.launch { // TODO: move the "viewModelScope.launch"s to the init
            restaurantRepository.refreshRestaurantMenu(restaurantId)
        }
    }



    val menuUiState: StateFlow<MenuPreviewUiState> = restaurantRepository.observeMenuPagesByRestaurant(restaurantId)    //TODO: Later, it's going to receives the dishes also to follow the new version of the UI (See in Canva)
        .map { pages ->
            if (pages.isNotEmpty()) {
                MenuPreviewUiState.Success(
                    RestaurantInfoUiModel(
                        menuImageUrls = pages.map { it.imageUrl }   //TODO: In later versions, it's going to use a .toUiModel
                    )
                )
            }
            else {
                MenuPreviewUiState.Error("Restaurant menu not found yet")
            }


        }
        .onStart {
            emit(MenuPreviewUiState.Loading)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MenuPreviewUiState.Loading
        )

    fun onHomeClicked(){
        viewModelScope.launch {
            _effect.send(MenuPreviewUiEvents.NavigateToHome)
        }
    }

    fun onYouClicked(){
        viewModelScope.launch {
            _effect.send(MenuPreviewUiEvents.NavigateToYou)
        }
    }

    fun onContactClicked(){
        viewModelScope.launch {
            _effect.send(MenuPreviewUiEvents.NavigateToContact)
        }
    }
}