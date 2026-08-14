package com.example.yummiziapp.features.intro.ui.model

data class CarouselUiState(
    val items: List<CarouselItemUiModel> = emptyList(),
//    val isLoading: Boolean = true,
    val activePageIndex: Int = 0
)