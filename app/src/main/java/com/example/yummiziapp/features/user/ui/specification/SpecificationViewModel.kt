package com.example.yummiziapp.features.user.ui.specification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.core.data.local.entities.LoggedUser
import com.example.yummiziapp.core.data.local.entities.Restaurant
import com.example.yummiziapp.core.data.local.model.RequestStatusToken
import com.example.yummiziapp.features.request.domain.repository.RequestRepository
import com.example.yummiziapp.features.restaurant.domain.repository.RestaurantRepository
//import com.example.yummiziapp.features.restaurant.ui.model.RestaurantIdentityUiModel
import com.example.yummiziapp.features.user.domain.repository.UserRepository
import com.example.yummiziapp.features.user.ui.model.SelectedRestaurantUiModel
import com.example.yummiziapp.features.user.ui.model.FocusedRestaurantUiModel
import com.example.yummiziapp.features.user.ui.model.PendingRequestUiStatus
import com.example.yummiziapp.features.user.ui.model.RestaurantIdentityUiModel
import com.example.yummiziapp.features.user.ui.model.UserDetailsUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class SpecificationViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val requestRepository: RequestRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    init{
        viewModelScope.launch {
            getUpdatedRequestStatus()
        }
    }

    private suspend fun getUpdatedRequestStatus(){
        requestRepository.fetchUpdatedRequestStatuses()
    }

    private val restaurantIdString: String? = savedStateHandle["restaurantId"]
    private val restaurantIdReceivedFromNavigation = restaurantIdString?.let { Uuid.parseOrNull(it) }

    // The Reactive Engine Core: Wrap the ID inside a StateFlow
    private val _focusedRestaurantId = MutableStateFlow(restaurantIdReceivedFromNavigation) // TODO: consider to move it to the stateFlow that is bellow
//    val focusedRestaurantId: StateFlow<Uuid?> = _focusedRestaurantId.asStateFlow()

    private val initialRestaurantIdToRemove: Uuid? = null
    private val _lastRestaurantIdThatTheyTriedRemove = MutableStateFlow(initialRestaurantIdToRemove)

    @OptIn(ExperimentalCoroutinesApi::class)
    val restaurantIdentityUiState: StateFlow<RestaurantIdentityUiModel> = _lastRestaurantIdThatTheyTriedRemove
        .flatMapLatest { restaurantId ->
            if(restaurantId == null){
                flowOf(RestaurantIdentityUiModel())
            }
            else{
                restaurantRepository    /// Modify this state if more attributes of the restaurant is needed
                    .observeRestaurantById(restaurantId)
                    .map { restaurant ->
                        if(restaurant == null) {
                            RestaurantIdentityUiModel()
                        }
                        else{
                            RestaurantIdentityUiModel(
                                restaurantId = restaurant.id,
                                imageUrl = restaurant.logoUrl,
                                name = restaurant.name
                            )
                        }
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RestaurantIdentityUiModel()
        )

    fun onTryToRemoveRequest(restaurantId: Uuid){
        _lastRestaurantIdThatTheyTriedRemove.update{ restaurantId }
    }

    fun leaveQueue(restaurantId: Uuid?) {  /// The restaurant ID can be got from the UI state and not as parameter, consider it as an alternative option

        viewModelScope.launch {
            try {
                if(restaurantId != null){
                    requestRepository.leaveQueue(restaurantId)
                }
            } catch (e: Exception) {
                // This is where you tell the user the "Optimistic" part failed
                Log.d("MyActivity", "🚩🚮 There was an error while trying to leave the queue  | $e\n")
                //_effect.send(HomeScreenUiEvents.ShowError("Check your connection. Could not leave queue."))

            }
        }
    }

    // 🔄 2. The Main UI State Machine Pipeline
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiRestaurantState: StateFlow<RestaurantSpecificationUiState> = _focusedRestaurantId
        .flatMapLatest { restaurantId ->
            // Whenever _focusedRestaurantId updates its value, this block re-runs automatically!
            if (restaurantId != null) {
                Log.d("MyActivity", "🔎👉 Switching state mode to FOCUSED: $restaurantId")

                restaurantRepository.observePendingRestaurantWithStatusById(restaurantId)   /// Is it necessary to have an "observePendingRestaurantWithStatusById" if instead of a generic "observeRestaurantWithStatusById"
                    .combine(requestRepository.observeRequestByRestaurantId(restaurantId)) { currentRestaurant, associatedRequest ->
                        if (currentRestaurant == null) {
                            RestaurantSpecificationUiState.Error("Restaurant data allocation corrupted or missing.")
                        } else {
                            RestaurantSpecificationUiState.FocusingRestaurant(
                                currentRestaurant.restaurant.toFocusedRestaurantUiModel(
                                    currentRestaurant.requestStatus.toUiStatus(),
                                    associatedRequest?.arrivalTimeInMinutes ?: 15
                                )
                            )
                        }
                    }
            } else {
                Log.d("MyActivity", "🔎👉 Switching state mode to LISTING")

                restaurantRepository.observePendingRestaurantsWithStatus()
                    .map { currentSelectedRestaurants ->
                        RestaurantSpecificationUiState.ListingRestaurants(
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
            emit(RestaurantSpecificationUiState.Error(error.message ?: "An unexpected layout error occurred."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RestaurantSpecificationUiState.Loading
        )

    val uiUserState: StateFlow<UserSpecificationUiState> = userRepository   // TODO:  Divide it in company and specifics
        .observeCurrentUserInfo()
        .combine(
            requestRepository.observeIfThereAreRequests()
        ){ userInfo, thereIsSomeRequestActive ->
            if (userInfo == null) {
                Log.w("MyActivity", "⚠️  User details are null")
                UserSpecificationUiState.Success( UserDetailsUiModel() )
            }
            else{
                UserSpecificationUiState.Success(userInfo.toUserDetailsUiModel(thereIsSomeRequestActive))
            } as UserSpecificationUiState
        }
        .catch { e -> emit(UserSpecificationUiState.Error(e.message ?: "Unknown Error when trying to find user's details")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSpecificationUiState.Loading
        )

    private fun Restaurant.toFocusedRestaurantUiModel(status: PendingRequestUiStatus, customerArrivalTimeInMinutes: Int) = FocusedRestaurantUiModel(
        restaurantId = id,
        restaurantLogoUrl = logoUrl,
        waitingState = status,
        distanceKm = 10,    // TODO: Remove mockup
        name = name,
        customerArrivalTimeInMinutes = customerArrivalTimeInMinutes,
        MaxWaitingTimeInMinutesByRestaurant = 15 // TODO: It will be unmocked when the restaurant is able to select its own waiting time
    )

    // Extension function for clean mapping
    // Updated Mapper
    private fun Restaurant.toSelectableRestaurantUiModel(status: PendingRequestUiStatus) = SelectedRestaurantUiModel(
        restaurantId = id,
        distanceKm = 10,    // TODO: Remove mockup
        restaurantLogoUrl = logoUrl,
        waitingState = status // Dynamic status from the DB!
    )

    // 🚀 High-Performance Pattern Matrix: Extension Mapper
    private fun RequestStatusToken.toUiStatus(): PendingRequestUiStatus = when (this) {
        RequestStatusToken.IDLE -> PendingRequestUiStatus.Idle
        RequestStatusToken.QUEUED -> PendingRequestUiStatus.Queued
        RequestStatusToken.ACCEPTED -> PendingRequestUiStatus.Accepted
        RequestStatusToken.DECLINED -> PendingRequestUiStatus.Declined
        else -> PendingRequestUiStatus.Other    // TODO: Handle a better solution for this
    }

    private fun LoggedUser.toUserDetailsUiModel( thereIsSomeRequestActive: Boolean) = UserDetailsUiModel(
        specifics = specifics,
        companySize = companySize,
        companySizeIsFixed = thereIsSomeRequestActive
    )


    private val _effect = Channel<SpecificationUiEvents>(Channel.BUFFERED)
    val navEffect = _effect.receiveAsFlow()


    // fun onGoBackClicked() {  // TODO
    //      /// Unfocus the state of the focused restaurant
    // }


    /// If the restaurant is still in "QUEUED", go to the focused restaurant details (arrival control)
    /// If the restaurant accepted the table request, open the confirmation pop-up
    /// If the restaurant is "PAID", this "onRestaurantClicked" doesn't have any sense because the app
    //  will show the focused restaurant directly with the confirmed arrival details

    /// They do the same thing, but I will keep it like this in case there are specific actions later for some of them
    fun onQueuedRestaurantClicked(id: Uuid) {
        _focusedRestaurantId.value = id
    }
    fun onAcceptedRestaurantClicked(id: Uuid) {
        _focusedRestaurantId.value = id
    }
    fun onDeclinedRestaurantClicked(id: Uuid) {
        _focusedRestaurantId.value = id
    }

    fun onAcceptanceConfirmed(){
        // 🚀 STEP 1: Secure the ID snapshot immediately on the main thread
        val restaurantId = _focusedRestaurantId.value

        if (restaurantId == null) {
            Log.e("MyActivity", "🚩  Aborting confirmation: No active restaurant focused.")
            return
        }
        viewModelScope.launch {

            try {
                // Send acceptance confirmation to the backend network/API and disable the current requests.
                requestRepository.confirmAcceptance(restaurantId)

                // Fire the navigation side-effect safely using the frozen ID
                _effect.send(SpecificationUiEvents.NavigateToRestaurantMenu(restaurantId))

            } catch (error: Exception) {
                // Handle network or database transaction failures cleanly
//                _effect.send(SpecificationUiEvents.ShowErrorSnackbar(error.message ?: "Transaction failed"))
            }

        }
    }

    fun onQueueJoiningClicked(arrivalTimeInMinutes: Int, restaurantId: Uuid) {

        Log.d("MyActivity", "✅👥  Request made for $restaurantId with $arrivalTimeInMinutes minutes")

        viewModelScope.launch {
            requestRepository.createQueueRequest(restaurantId, arrivalTimeInMinutes)
            _effect.send(SpecificationUiEvents.NavigateToHome)
        }
    }

    fun onCompanySizeUpdated(isIncreasing: Boolean = true){
        viewModelScope.launch {
            userRepository.updateCompanySize(isIncreasing)
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

    fun onContactClicked() {
        viewModelScope.launch {
            _effect.send(SpecificationUiEvents.NavigateToContact)
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