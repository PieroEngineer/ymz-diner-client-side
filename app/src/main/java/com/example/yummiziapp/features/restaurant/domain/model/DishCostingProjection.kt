package com.example.yummiziapp.features.restaurant.domain.model

//import androidx.annotation.Keep
import com.example.yummiziapp.core.data.local.model.DishTypeToken

//@Keep
data class DishCostingProjection(
    val name: String,
    val priceCents: Int,
    val type: DishTypeToken = DishTypeToken.MAIN,
//    val isOutOfStock: Boolean = false, /// Consider it
)
