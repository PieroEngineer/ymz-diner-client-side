package com.example.yummiziapp.features.intro.ui.model

data class CarouselItemUiModel(
//    val id: String,
    val phrase: String,
    val imageId: Int? = null,
    val isLast: Boolean = false,
    val attachedVideoUrl: String? = null
)
