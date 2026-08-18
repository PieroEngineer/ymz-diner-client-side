package com.example.yummiziapp.features.restaurant.ui.food

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.core.data.local.entities.Dish
import com.example.yummiziapp.core.data.local.entities.MenuPage
import com.example.yummiziapp.core.utils.toCurrencyFormat
import com.example.yummiziapp.features.payment.domain.repository.PaymentRepository
import com.example.yummiziapp.features.request.domain.repository.RequestRepository
import com.example.yummiziapp.features.restaurant.domain.repository.RestaurantRepository
import com.example.yummiziapp.features.restaurant.ui.model.CoordinatesInPage
import com.example.yummiziapp.features.restaurant.ui.model.FoodSelectionUiModel
import com.example.yummiziapp.features.restaurant.ui.model.MenuUiModel
import com.example.yummiziapp.features.restaurant.ui.model.RestaurantIdentityUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.sumOf
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.uuid.Uuid

@HiltViewModel
class FoodSelectionViewModel @Inject constructor(   //TODO: Change the way of how the dishes change to don't receive all of them in a
    private val restaurantRepository: RestaurantRepository,
    private val requestRepository: RequestRepository,
    private val paymentRepository: PaymentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _effect = Channel<FoodSelectionUiEvents>(Channel.BUFFERED)
    val navEffect = _effect.receiveAsFlow()

    // Extract the ID safely
    private val restaurantIdString: String = checkNotNull(savedStateHandle["restaurantId"])
    private val restaurantId = Uuid.parse(restaurantIdString)

    private val _orderMap = MutableStateFlow<Map<Uuid, Int>>(emptyMap())

    init {
        viewModelScope.launch {
            // Trigger the GET request to cloud immediately on start
            refreshMenu()
            initializeOrder()   // To allow us to use a local map and minimize room overhead when update the order frequently
        }
    }

    private suspend fun refreshMenu() {
        restaurantRepository.refreshRestaurantMenu(restaurantId)
    }

    private suspend fun initializeOrder() {
        val currentOrder = paymentRepository.getCurrentOrderSelections()
        if (currentOrder.isNotEmpty()) {
            _orderMap.update {
                currentOrder.associate { it.dishId to it.quantity }
            }
        }
    }

    // Create an observer that give the "localCreationTimestamp" of "Request" entity and return it to the UI for the clock
     val selectionFinishedTimestamp: StateFlow<Instant> = requestRepository.observeRequestByRestaurantId(restaurantId)
      .map { request ->
          if(request != null && request.localConfirmationTimestamp != null) {
            request.localConfirmationTimestamp.plus(5.minutes)
          }
          else {
            Clock.System.now()
          }
      }
      .stateIn(
                 scope = viewModelScope,
                 started = SharingStarted.WhileSubscribed(5000),
                 initialValue = Clock.System.now()
             )

    fun onContactRestaurant(){
        viewModelScope.launch {
            val currentRestaurant = restaurantRepository.getRestaurantById(restaurantId)
            if (currentRestaurant != null){
                _effect.send(FoodSelectionUiEvents.IntentContactRestaurant(
                    "https://wa.me/51${currentRestaurant.phoneNumberToContact}?text=Hola%2C%20vengo%20desde%20la%20aplicaci%C3%B3n%20de%20Yummizi%2C%20quisiera%20comentarle%20que%20"
                ))
            }
        }
    }

    fun confirmOrder(){
        viewModelScope.launch {
            paymentRepository.selectOrder(_orderMap.value)

            requestRepository.notifyAttemptOfPayment(restaurantId)   // It also works as a tester of good connection before go to pay
                .onSuccess{
                    Log.d("RequestRepository", "🔎☝️  Notification was sent")
                    _effect.send(FoodSelectionUiEvents.NavigateToPayment(restaurantId) )    /// Should I send this contained in the entity or here?
                }
                .onFailure{ throwable ->
                    Log.e("FoodSelectionViewModel", "🚩 Error confirming order", throwable)
                    _effect.send(FoodSelectionUiEvents.ShowSnackbar(message = "Hubo un problema de conexión")) // TODO: Use a better message
                }
        }
    }

    fun cancelRequest(){    /// Also handle the Order cancelation if in later updates this screen create Order items in its inizialitation
        viewModelScope.launch {
            //Remove current request
            requestRepository.cancelRequest(restaurantId)

            //Navigate to home
            _effect.send(FoodSelectionUiEvents.NavigateToHome)

        }   // TODO: Clean the stack too
    }


    // 3. Methods to update state
    fun updateQuantity(dishId: Uuid, delta: Int) {
        _orderMap.update { current ->
            val newCount = (current[dishId] ?: 0) + delta
            if (newCount <= 0) current else current + (dishId to newCount) /// Consider change "current - dishId" by the intention of this "current[dishId] = 1"
        }
    }

    fun removeDish(dishId: Uuid) {
        _orderMap.update { current ->
            current - dishId
        }
    }

    // High-Performance StateFlow
    // We observe the database. If the network refresh succeeds later,
    // this Flow automatically emits the new data.
    val menuUiState: StateFlow<FoodSelectionUiState> = restaurantRepository
        .observeRestaurantMenuWithAvailableDishes(restaurantId)
        .map { map ->
            // Transform the map to the UI model list
            val uiModels = map.toUiModels()

            if (uiModels.isEmpty()) FoodSelectionUiState.Loading
            else FoodSelectionUiState.Success(uiModels)
        }
        .catch { e -> emit(FoodSelectionUiState.Error(e.message ?: "Unknown Error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FoodSelectionUiState.Loading
        )


    // The Source of Truth (Items)
    val foodSelectionState: StateFlow<List<FoodSelectionUiModel>> = restaurantRepository
        .observeDishes(restaurantId)
        .combine(_orderMap) { dishes, selection ->
            // Create a lookup map for efficient detail retrieval
            val dishLookup = dishes.associateBy { it.id }

            // Iterate over 'selection' to maintain the order in which items were added
            selection.mapNotNull { (dishId, quantity) ->
                val dish = dishLookup[dishId] ?: return@mapNotNull null
                if (quantity <= 0) return@mapNotNull null

                FoodSelectionUiModel(
                    id = dish.id,
                    name = dish.name,
                    amount = quantity,
                    unitPriceCents = dish.priceCents,
                    accumulatedCost = (quantity * dish.priceCents).toCurrencyFormat()
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val restaurantIdentityUiState: StateFlow<RestaurantIdentityUiModel> = restaurantRepository    /// Modify this state if more attributes of the restaurant is needed
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
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RestaurantIdentityUiModel()
        )

    // The Derived State (Total) - No new DB query needed!
    val totalOrderCost: StateFlow<String> = foodSelectionState
        .map { selectionList ->
            val totalCents = selectionList.sumOf {
                it.amount * it.unitPriceCents.toLong()  /// Is it necessary this "toLong"?
            }
            totalCents.toInt().toCurrencyFormat()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "0.00"
        )
    //TODO: later, change it to this approach
    //data class CheckoutUiState(
    //    val totalCents: Long = 0L,           // Use this for logic/validation
    //    val totalFormatted: String = "",     // Use this for Text()
    //    val canCheckout: Boolean = false     // Logic: totalCents >= minOrder
    //)
    //val checkoutState: StateFlow<CheckoutUiState> = foodSelectionState
    //    .map { items ->
    //        val rawTotal = items.sumOf { it.amount * it.unitPriceCents.toLong() }
    //        val minOrder = 2000L // 20.00
    //
    //        CheckoutUiState(
    //            totalCents = rawTotal,
    //            totalFormatted = CurrencyFormatter.formatCentsToSoles(rawTotal.toInt()),
    //            canCheckout = rawTotal >= minOrder
    //        )
    //    }
    //    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CheckoutUiState())



    fun Map<MenuPage, List<Dish>>.toUiModels(): List<MenuUiModel> {
        return this.map { (page, dishes) ->
            MenuUiModel(
                page = page,    //TODO: Don't give the entity as such, just the necessary attributes
                coordinatesInPages = dishes.map {
                    CoordinatesInPage(
                        it.coordinateXInMenu.toFloat(),
                        it.coordinateYInMenu.toFloat()
                    )
                },
                dishIds = dishes.map { it.id }  /// Consider don't use lists, but UI models as parameters
            )
        }.sortedBy { it.page.pageNumber }
    }
}



//The "Session Manager" Pattern (Recommended)
//You should have a SessionManager or UserStorage class (usually powered by DataStore or SharedPreferences) that holds the current logged-in user's info.

//TODO: Important consideration
// Security Check: The "Final Handshake"
// When the user clicks "Pay," your app should not send the price to the server.
//
//  + Bad: api.pay(amount = 10.50) (The user could use a proxy tool to change 10.50 to 0.01!).
//
//  + Good: api.createOrder(items = listOf(dishIds))
//
// The Pro Way: The server receives the list of IDs, looks up the prices in its own database, calculates the total itself, and then asks the user for that specific amount.