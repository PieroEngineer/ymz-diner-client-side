package com.yummizi.app.features.restaurant.domain.model

//import androidx.annotation.Keep
import com.yummizi.app.core.data.local.model.DishTypeToken

//@Keep
data class DishCostingProjection(
    val name: String,
    val priceCents: Int,
    val type: DishTypeToken = DishTypeToken.MAIN,
//    val isOutOfStock: Boolean = false, /// Consider it
)
