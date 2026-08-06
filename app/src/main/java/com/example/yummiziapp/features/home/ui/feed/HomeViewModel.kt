package com.example.yummiziapp.features.home.ui.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.core.data.local.entities.Restaurant
import com.example.yummiziapp.core.data.local.model.RequestStatusToken
import com.example.yummiziapp.features.user.domain.repository.UserRepository
import com.example.yummiziapp.features.home.domain.usecase.GetCategorizedFeedUseCase
import com.example.yummiziapp.features.home.ui.model.SectionUiModel
import com.example.yummiziapp.features.restaurant.domain.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.Uuid

// Idea: don't search every new character, but every 2 or 3 characters (when the word is small or big enought)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategorizedFeed: GetCategorizedFeedUseCase,
    private val restaurantRepository: RestaurantRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    // Navigation handler
    private val _effect = Channel<HomeUiEvents>(Channel.BUFFERED)
    val navEffect = _effect.receiveAsFlow()

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)   /// Is this way a bad practice?
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch { /// I guess this launch scope is unnecessary because the functions included already have it.

            // Clean all restaurants
            Log.d("MyActivity", "🧹  Cleaning all restaurants...\n")
            clearFeed()   //TODO: Remove this in production. The behavior should cover certain situations. Consider that the cleaning is when the user doesn't enter after a long while

            Log.d("MyActivity", "🎨  Trying to initialize data for user and restaurant...\n")
            userRepository.seedMockUser() // Make this a suspend call
            //TODO: Update the existing requests in the cloud, so the orange frame will be received, but we already made a logic to avoid it asks for all request every time the user enters from the same phone
            syncFeed()
            observeFeed()
        }
    }

    private suspend fun observeFeed() { // TODO: Change it be a flow and avoid the direct modification of "_uiState.value"
            getCategorizedFeed().collect { categoryMap ->
                Log.d("MyActivity", "🗺️  categoryMap received in the view model from use case\n$categoryMap\n")

                val sections = categoryMap.entries.mapIndexed { index, entry ->
                    val type = entry.key
                    val restaurants = entry.value
                    SectionUiModel(
                        id = index,
                        title = type,
                        restaurants = restaurants,
                        restaurantBlockHeight = 120
                    )
                }
                _uiState.value = HomeUiState.Success(sections)
            }
    }

    // TODO
    // The current logic that I think know is that the pending states are "QUEUED", "ACCEPTED" and "DENIED".
    // On the other hand, if there is at least one "PAID", the restaurant selection screen only show that
    // restaurant in the white frame as with stated in Canva

    // This is to update the orange frame
    val activeRestaurantsUiState: StateFlow<ActiveRestaurantsUiState> = combine(
        restaurantRepository.observePendingRestaurants(),
        restaurantRepository.observeNoPendingRestaurantWithStatus()
    ) { pendingRestaurants, noPendingRestaurant ->
        if (noPendingRestaurant == null) {
            Log.d("MyActivity", "Restaurant with status is null, so trasnmit pending restaurants: $pendingRestaurants")

            ActiveRestaurantsUiState.PendingStatus(pendingRestaurants)
        }
        else{
            Log.d("MyActivity", "Restaurant with status is not null, so trasnmit ready restaurant: $noPendingRestaurant")
            ActiveRestaurantsUiState.ReadyStatus(noPendingRestaurant)
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ActiveRestaurantsUiState.Loading
    )

    fun onSelectionFrameClicked(){
        viewModelScope.launch {
            if (activeRestaurantsUiState.value is ActiveRestaurantsUiState.ReadyStatus){

                val currentState = activeRestaurantsUiState.value as? ActiveRestaurantsUiState.ReadyStatus ?: return@launch

                if(currentState.restaurantWithStatus.requestStatus == RequestStatusToken.PAID){
                    _effect.send(HomeUiEvents.NavigateToPaidRestaurant( currentState.restaurantWithStatus.restaurant.id ))
                }
                else {
                    _effect.send(HomeUiEvents.NavigateToReadyRestaurantMenu( currentState.restaurantWithStatus.restaurant.id ))
                }
            }
            else{
                _effect.send(HomeUiEvents.NavigateToRestaurantSelectionFrame(null))
            }
        }
    }

    private suspend fun clearFeed() {
        try {
            Log.d("MyActivity", "🧹  Cleaning all restaurants...\n")
            restaurantRepository.clearAllRestaurantsAndRequests()
        }
        catch (e: Exception) {
            Log.d("MyActivity", "🚩🧹 There was an error while trying to clean the feed  | $e\n")
        }
    }

    private suspend fun syncFeed() {
        try {
            Log.d("MyActivity", "🔁 Refreshing restaurant feed\n")
            restaurantRepository.syncFeedRestaurants()
        }
        catch (e: Exception) {
            Log.d("MyActivity", "🚩🔁 There was an error while trying to refresh the feed  | $e\n")
        }
    }


    fun onRestaurantClicked(id: Uuid) {
        viewModelScope.launch {
            _effect.send(HomeUiEvents.NavigateToRestaurantMenuPreview(id))
        }
    }


    fun onYouClicked(){
        viewModelScope.launch {
            _effect.send(HomeUiEvents.NavigateToPreferences)
        }
    }


    fun onRestaurantDropped(restaurant: Restaurant) {
        // We use viewModelScope because writing to a database
        // is a 'suspend' (heavy) operation.
        viewModelScope.launch {
            _effect.send(HomeUiEvents.NavigateToRestaurantSelectionFrame(restaurant.id))

        }
    }
}