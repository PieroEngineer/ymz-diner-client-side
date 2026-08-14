package com.example.yummiziapp.features.intro.ui.intro

import androidx.lifecycle.ViewModel
import com.example.yummiziapp.features.intro.ui.model.CarouselItemUiModel
import com.example.yummiziapp.features.intro.ui.model.CarouselUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.example.yummiziapp.R

@HiltViewModel
class IntroViewModel @Inject constructor(

) : ViewModel() {

    private val _effect = Channel<IntroUiEvent>(Channel.BUFFERED)
    val navEffect = _effect.receiveAsFlow()

    private val _uiState = MutableStateFlow(
        CarouselUiState(
            items = listOf(
                CarouselItemUiModel(
                    phrase = "Encuentra restaurantes cercanos",
                    imageId = R.drawable.restaurant_from_phone_image,
                    isLast = false,
                    attachedVideoUrl = null,
                ),
                CarouselItemUiModel(
                    phrase = "Selecciona tu orden",
                    imageId = R.drawable.discovering_dishes_image,
                    isLast = false,
                    attachedVideoUrl = null,
                ),
                CarouselItemUiModel(
                    phrase = "Preparación oportuna",
                    imageId = R.drawable.double_thinking_in_cooking__image,
                    isLast = false,
                    attachedVideoUrl = null,
                )
            )
        )
    )
    val uiState: StateFlow<CarouselUiState> = _uiState.asStateFlow()


    fun onPageChanged(newIndex: Int) {
        _uiState.update { it.copy(activePageIndex = newIndex) }
        // Execute side-effects like pre-fetching next items or tracking analytics here
    }
}