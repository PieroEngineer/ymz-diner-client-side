package com.yummizi.app.features.intro.ui.model

data class CarouselItemUiModel(
//    val id: String,
    val phrase: String,
    val imageId: Int,
    val isLast: Boolean = false,
//    val attachedVideoUrl: String? = null // TODO: Find the way to implement it.
)
