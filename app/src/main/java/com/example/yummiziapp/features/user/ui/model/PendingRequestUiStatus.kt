package com.example.yummiziapp.features.user.ui.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.example.yummiziapp.R

@Immutable // 🚀 Boosts UI rendering speeds by allowing Compose to skip unchanged items!
sealed interface PendingRequestUiStatus {
    val phrase: String
    @get:DrawableRes
    val iconRes: Int // Guarantees compile safety for asset references

    data object Idle : PendingRequestUiStatus {
        override val phrase = "Restaurante no seleccionado"
        override val iconRes = 0      //TODO: Change the icons like this later
    }

    data object Queued : PendingRequestUiStatus {
        override val phrase = "Esperando\nrespuesta..."
        override val iconRes = R.drawable.in_queue_icon
    }

    data object Accepted : PendingRequestUiStatus {
        override val phrase = "Hay mesas\npara ti"
        override val iconRes = R.drawable.enter_icon
    }

    //X
//    data object Paid : RequestUiStatus {
//        override val phrase = "Esperando tu llegada"
//        override val iconRes = 0
//    }

    data object Declined : PendingRequestUiStatus {
        override val phrase = "No disponible\npor ahora"
        override val iconRes = 0    // TODO
    }

    data object Other : PendingRequestUiStatus {
        override val phrase = ""
        override val iconRes = 0
    }

    /// Consider that "cancel" shouldn't be here
}