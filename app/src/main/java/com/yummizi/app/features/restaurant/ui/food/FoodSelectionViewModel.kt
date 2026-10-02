package com.yummizi.app.features.restaurant.ui.food

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yummizi.app.core.data.local.entities.Dish
import com.yummizi.app.core.data.local.entities.MenuPage
import com.yummizi.app.core.data.local.model.DishTypeToken
import com.yummizi.app.core.utils.toCurrencyFormat
import com.yummizi.app.features.payment.domain.repository.PaymentRepository
import com.yummizi.app.features.request.domain.repository.RequestRepository
import com.yummizi.app.features.restaurant.domain.repository.RestaurantRepository
import com.yummizi.app.features.restaurant.ui.model.ComplementSelectionUiState
import com.yummizi.app.features.restaurant.ui.model.ExtraCommentariesUiState
import com.yummizi.app.features.restaurant.ui.model.ComplementaryDishUiModel
import com.yummizi.app.features.restaurant.ui.model.CoordinatesInPage
import com.yummizi.app.features.restaurant.ui.model.FoodSelectionUiModel
import com.yummizi.app.features.restaurant.ui.model.MenuUiModel
import com.yummizi.app.features.restaurant.ui.model.RestaurantIdentityUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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

    private val _complementSelectionUiState = MutableStateFlow(ComplementSelectionUiState())
    val complementSelectionUiState: StateFlow<ComplementSelectionUiState> = _complementSelectionUiState.asStateFlow()

    private val _extraCommentariesUiState = MutableStateFlow(ExtraCommentariesUiState())
    val extraCommentariesUiState: StateFlow<ExtraCommentariesUiState> = _extraCommentariesUiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Trigger the GET request to cloud immediately on start
            refreshMenu()

            generateOrder()
        }
    }

    private suspend fun refreshMenu() { // TODO: Move it to "onStart" of some flow
        restaurantRepository.refreshRestaurantMenu(restaurantId)
    }

    private suspend fun generateOrder(){
        restaurantRepository.generateNewOrder()
    }

    // Create an observer that give the "localCreationTimestamp" of "Request" entity and return it to the UI for the clock
     val selectionFinishedTimestamp: StateFlow<Instant> = requestRepository.observeRequestByRestaurantId(restaurantId)
      .map { request ->
          if(request != null && request.onContinueAcceptanceLocalTimestamp != null) {
            request.onContinueAcceptanceLocalTimestamp.plus(5.minutes)
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
                Log.d("FoodSelectionViewModel", "Current restaurant\n${currentRestaurant}")
                _effect.send(FoodSelectionUiEvents.IntentContactRestaurant(
                    "https://wa.me/${currentRestaurant.phoneNumberToContact}?text=Hola%2C%20vengo%20desde%20la%20aplicaci%C3%B3n%20de%20Yummizi%2C%20quisiera%20comentarle%20que%20"
                ))

            }
        }
    }

    fun tryToSelectOrder(){
        viewModelScope.launch {

            if(foodSelectionState.value.isEmpty()){
                _effect.send(FoodSelectionUiEvents.ShowSnackbar(message = "Por favor, seleccione al menos un plato"))
            }
            else{
                // Open a modal to include diner's commentaries
                _extraCommentariesUiState.update {
                    it.copy(isModalOpen = !it.isModalOpen)
                }
            }
        }
    }

    fun selectOrder(){
        viewModelScope.launch {
            paymentRepository.updateExtraCommentaries(_extraCommentariesUiState.value.extraCommentaries)

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

    fun toggleExtraCommentariesModalOpening(){
        _extraCommentariesUiState.update {
            it.copy(isModalOpen = !it.isModalOpen)
        }
    }

    fun updateExtraCommentaries(extraCommentaries: String){
        _extraCommentariesUiState.update {
            it.copy(extraCommentaries = extraCommentaries)
        }
    }

    fun cancelRequest(){    /// Also handle the Order cancelation if in later updates this screen create Order items in its inizialitation
        viewModelScope.launch {
            Log.d("FoodSelectionViewModel", "🔎  Cancelling request")
            //Remove current request
            requestRepository.cancelRequest(restaurantId)

            //Navigate to home
            Log.d("FoodSelectionViewModel", "🔎  Navigating to home")
            _effect.send(FoodSelectionUiEvents.NavigateToHome)

        }   // TODO: Clean the stack too // Or not?
    }

    fun tryToIncludeASelection(dishId: Uuid){
        viewModelScope.launch {
            Log.d("FoodSelectionViewModel", "🔎🍽️  A food was selected")
            // Check if the dish have possible complementary
            val complementaryDishIds = restaurantRepository.getComplementaryDishesById(dishId)  // TODO (#492): Consider unify these repo's functions   -> "getAvailableComplementaryDishReferencesById"
//            Log.d("FoodSelectionViewModel", "🔎🍽️  Selected dish have these complement dish IDs $complementaryDishIds")

            val complementaryDishReferences = restaurantRepository.getAvailableReferencesByDishIds(complementaryDishIds)  // TODO (#492)
//            Log.d("FoodSelectionViewModel", "🔎🍽️  Complement dish references $complementaryDishReferences")

            // If have complementary -> Open modal to select them
            if (complementaryDishReferences.isNotEmpty()) {

                Log.d("FoodSelectionViewModel", "🔎🍽️  Complementary found")

                val mainDishName = restaurantRepository.getDishNameById(dishId)

//                Log.d("FoodSelectionViewModel", "🔎🍽️  Main dish name $mainDishName")

                _complementSelectionUiState.update {
                    ComplementSelectionUiState(
                        mainDishId = dishId,
                        availableComplementaryDishes = complementaryDishReferences.map {
                            ComplementaryDishUiModel(
                                dishId = it.dishId,
                                dishName = it.name
                            )
                        },
                        mainDishName = mainDishName,
                        isModalOpen = true
                    )
                }
            } else {   // If not -> Add to selection directly (Handle the situation of adding the same dish with the same complement)
                Log.d("FoodSelectionViewModel", "🔎🍽️  No complementary found")

                restaurantRepository.includeASelection(dishId)
            }
        }
    }

    fun includeASelection(dishId: Uuid, complementaryDishId: Uuid){
        viewModelScope.launch {
            restaurantRepository.includeASelection(dishId, complementaryDishId)
        }
    }

    fun toggleComplementSelectionModalOpening(){
        _complementSelectionUiState.update {
            it.copy(isModalOpen = !it.isModalOpen)
        }
    }

    // 3. Methods to update state
    fun updateQuantity(dishId: Uuid, complementaryDishId: Uuid, delta: Int) {  // It only works with existing selections

        viewModelScope.launch {
            // Get selection's current quantity
            val currentQuantity =
                restaurantRepository.getQuantityOfASelectionById(dishId, complementaryDishId)

            if (currentQuantity == 1 && delta == -1) return@launch

            // Update the selection's quantity
            restaurantRepository.updateOrderSelectionQuantity(dishId, complementaryDishId, delta)

        }
        //X
//        _orderMap.update { current ->
//            val newCount = (current[dishId] ?: 0) + delta
//            if (newCount <= 0) current else current + (dishId to newCount) /// Consider change "current - dishId" by the intention of this "current[dishId] = 1"
//        }

        // }

    }

    fun removeDish(dishId: Uuid, complementaryDishId: Uuid) {
        viewModelScope.launch {
            restaurantRepository.deleteOrderSelectionById(dishId, complementaryDishId)

            //X
            //        _orderMap.update { current ->
//            current - selectionId
//        }
        }
    }

    // High-Performance StateFlow
    // We observe the database. If the network refresh succeeds later,
    // this Flow automatically emits the new data.
    val menuUiState: StateFlow<MenuUiState> = restaurantRepository
        .observeRestaurantMenuWithAvailableDishes(restaurantId)
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


    val foodSelectionState: StateFlow<List<FoodSelectionUiModel>> = restaurantRepository.observeOrderSelections()
        .map{ /*dishes, */selections ->

            selections.map { orderSelection ->
                FoodSelectionUiModel(
                    dishId = orderSelection.dishId,
                    complementaryDishId = orderSelection.complementaryDishId,
                    name = orderSelection.dishName,
                    amount = orderSelection.quantity,
                    unitPriceCents = orderSelection.historicalUnitPriceInCents,
                    accumulatedCost = (orderSelection.quantity * orderSelection.historicalUnitPriceInCents).toCurrencyFormat()
                )

            }

//X
//            // Create a lookup map for efficient detail retrieval
//            val dishLookup = dishes.associateBy { it.id }
//
//            // Iterate over 'selection' to maintain the order in which items were added
//            selection.mapNotNull { (dishId, quantity) ->
//                val dish = dishLookup[dishId] ?: return@mapNotNull null
//                if (quantity <= 0) return@mapNotNull null
//
//                FoodSelectionUiModel(
//                    id = dish.id,
//                    name = dish.name,
//                    amount = quantity,
//                    unitPriceCents = dish.priceCents,
//                    accumulatedCost = (quantity * dish.priceCents).toCurrencyFormat()
//                )
//            }
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



    fun Map<MenuPage, List<Dish>>.toUiModels(): List<MenuUiModel> { // TODO: Consider change the map for other type made with data classes
        return this.map { (page, dishes) ->

            val mainDishes = dishes.filter { it.type == DishTypeToken.MAIN }

            MenuUiModel(
                page = page,    //TODO: Don't give the entity as such, just the necessary attributes
                coordinatesInPages = mainDishes.map {
                    CoordinatesInPage(
                        it.coordinateXInMenu.toFloat(),
                        it.coordinateYInMenu.toFloat()
                    )
                },
                dishIds = mainDishes.map { it.id }  /// Consider don't use lists, but UI models as parameters
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