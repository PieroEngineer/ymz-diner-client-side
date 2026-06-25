package com.example.yummiziapp.features.restaurant.ui.food

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.core.data.local.entities.Dish
import com.example.yummiziapp.core.data.local.entities.MenuPage
import com.example.yummiziapp.features.restaurant.domain.repository.RestaurantRepository
import com.example.yummiziapp.features.restaurant.ui.menu.MenuUiState
import com.example.yummiziapp.features.restaurant.ui.model.CoordinatesInPage
import com.example.yummiziapp.features.restaurant.ui.model.FoodSelectionUiModel
import com.example.yummiziapp.features.restaurant.ui.model.MenuUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.filter
import kotlin.collections.sumOf
import kotlin.uuid.Uuid

@HiltViewModel
class FoodSelectionViewModel @Inject constructor(   //TODO: Change the way of how the dishes change to don't receive all of them in a
    private val restaurantRepository: RestaurantRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Extract the ID safely
    private val restaurantIdString: String = checkNotNull(savedStateHandle["restaurantId"])
    private val restaurantId = Uuid.parse(restaurantIdString)


    private val _selectionMap = MutableStateFlow<Map<Uuid, Int>>(emptyMap())


    // 3. Methods to update state
    fun updateQuantity(dishId: Uuid, delta: Int) {
        val current = _selectionMap.value.toMutableMap()
        val newCount = (current[dishId] ?: 1) + delta
        if (newCount <= 0) current[dishId] = 1 else current[dishId] = newCount
        _selectionMap.value = current
    }

    // High-Performance StateFlow
    // We observe the database. If the network refresh succeeds later,
    // this Flow automatically emits the new data.
    val menuUiState: StateFlow<MenuUiState> = restaurantRepository
        .observeRestaurantMenuWithItsDishes(restaurantId)
        .map { map ->
            // Transform the map to the UI model list
            val uiModels = map.toUiModels()

            if (uiModels.isEmpty()) MenuUiState.Loading
            else MenuUiState.Success(uiModels)
        }
        .catch { e -> emit(MenuUiState.Error(e.message ?: "Unknown Error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MenuUiState.Loading
        )
    /// Possible improve here to be safe over coroutines
    //fun updateQuantity(dishId: Uuid, delta: Int) {
    //    _selectionMap.update { current ->
    //        val newCount = (current[dishId] ?: 0) + delta
    //        if (newCount <= 0) current - dishId else current + (dishId to newCount)
    //    }


    // The Source of Truth (Items)
    val foodSelectionState: StateFlow<List<FoodSelectionUiModel>> = restaurantRepository
        .observeDishes(restaurantId)
        .combine(_selectionMap) { dishes, selection ->
            dishes.map { dish ->
                val quantity = selection[dish.id] ?: 0
                val totalCents = quantity * dish.priceCents

                FoodSelectionUiModel(
                    id = dish.id,
                    name = dish.name,
                    amount = quantity,
                    unitPriceCents = dish.priceCents, // Carry this for the next step
                    accumulatedCost = CurrencyFormatter.formatCentsToSoles(totalCents)
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
                it.amount * it.unitPriceCents.toLong()
            }
            CurrencyFormatter.formatCentsToSoles(totalCents.toInt())
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


    init {
        // Trigger the GET request to cloud immediately on start
        refreshMenu()
    }


    private fun refreshMenu() {
        viewModelScope.launch {
            restaurantRepository.refreshRestaurantMenu(restaurantId)
        }
    }

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

object CurrencyFormatter {  /// Put it in utils folder
    fun formatCentsToSoles(cents: Int): String {
        val soles = java.math.BigDecimal(cents).movePointLeft(2)
        return java.text.DecimalFormat("0.00").format(soles)
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