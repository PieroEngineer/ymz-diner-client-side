package com.example.yummiziapp.features.restaurant.ui.preview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.yummiziapp.features.restaurant.domain.repository.RestaurantRepository
import com.example.yummiziapp.features.restaurant.ui.model.RestaurantInfoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map
import kotlin.uuid.Uuid

@HiltViewModel
class MenuPreviewViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Get the "restaurantId" from the Navigation Route string
    private val restaurantIdString: String = checkNotNull(savedStateHandle["restaurantId"])  // For one restaurant got

    private val restaurantId = Uuid.parse(restaurantIdString)

    init {
        // Trigger the GET request to cloud immediately on start
        refreshMenu()
    }


    private fun refreshMenu() {
        viewModelScope.launch {
            restaurantRepository.refreshRestaurantMenu(restaurantId)
        }
    }

    val menuState: StateFlow<MenuPreviewUiState> = restaurantRepository.observeMenuPagesByRestaurant(restaurantId)    //TODO: Later, it's going to receives the dishes also to follow the new version of the UI (See in Canva)
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
}