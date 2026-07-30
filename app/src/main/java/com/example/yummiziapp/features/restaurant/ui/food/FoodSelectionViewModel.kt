package com.example.yummiziapp.features.restaurant.ui.food

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.core.data.local.entities.Dish
import com.example.yummiziapp.core.data.local.entities.MenuPage
import com.example.yummiziapp.core.utils.toCurrencyFormat
import com.example.yummiziapp.features.payment.domain.repository.PaymentRepository
import com.example.yummiziapp.features.restaurant.domain.repository.RestaurantRepository
import com.example.yummiziapp.features.restaurant.ui.model.CoordinatesInPage
import com.example.yummiziapp.features.restaurant.ui.model.FoodSelectionUiModel
import com.example.yummiziapp.features.restaurant.ui.model.MenuUiModel
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
import kotlin.collections.filter
import kotlin.collections.sumOf
import kotlin.uuid.Uuid

@HiltViewModel
class FoodSelectionViewModel @Inject constructor(   //TODO: Change the way of how the dishes change to don't receive all of them in a
    private val restaurantRepository: RestaurantRepository,
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
        // Trigger the GET request to cloud immediately on start
        refreshMenu()
        initializeOrder()
    }

    private fun refreshMenu() {
        viewModelScope.launch {
            restaurantRepository.refreshRestaurantMenu(restaurantId)
        }
    }

    private fun initializeOrder() {
        viewModelScope.launch {
            val currentOrder = paymentRepository.getCurrentOrder()
            if (currentOrder.isNotEmpty()) {
                _orderMap.update { 
                    currentOrder.associate { it.dishId to it.quantity }
                }
            }
        }
    }

    fun confirmOrder(){
        viewModelScope.launch {
            paymentRepository.selectOrder(_orderMap.value)

            // TODO: If the payment fails, Navigate, but show a snackbar asking for trying again.
            _effect.send(FoodSelectionUiEvents.NavigateToPayment(restaurantId) )    /// Should I send this contained in the entity or here?
        }
    }


    // 3. Methods to update state
    fun updateQuantity(dishId: Uuid, delta: Int) {
        _orderMap.update { current ->
            val newCount = (current[dishId] ?: 0) + delta
            if (newCount <= 0) current - dishId else current + (dishId to newCount) /// Consider change "current - dishId" by the intention of this "current[dishId] = 1"
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
        .observeRestaurantMenuWithItsDishes(restaurantId)
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
            dishes.map { dish ->
                val quantity = selection[dish.id] ?: 0
                val totalCents = quantity * dish.priceCents

                FoodSelectionUiModel(
                    id = dish.id,
                    name = dish.name,
                    amount = quantity,
                    unitPriceCents = dish.priceCents, // Carry this for the next step
                    accumulatedCost = totalCents.toCurrencyFormat()
                )
            }.filter { it.amount > 0 }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
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
                dishIds = dishes.map { it.id }
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