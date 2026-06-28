package com.example.yummiziapp.features.user.ui.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.example.yummiziapp.R

@Immutable // 🚀 Boosts UI rendering speeds by allowing Compose to skip unchanged items!
sealed interface RequestUiStatus {
    val phrase: String
    @get:DrawableRes
    val iconRes: Int // Guarantees compile safety for asset references

    data object Idle : RequestUiStatus {
        override val phrase = "Restaurant no seleccionado"
        override val iconRes = 0      //TODO: Change the icons like this later
    }

    data object Queued : RequestUiStatus {
        override val phrase = "Esperando\nrespuesta..."
        override val iconRes = R.drawable.in_queue_icon
    }

    data object Accepted : RequestUiStatus {
        override val phrase = "Hay mesas\npara ti"
        override val iconRes = R.drawable.enter_icon
    }

    data object Paid : RequestUiStatus {
        override val phrase = "Esperando tu llegada"
        override val iconRes = 0
    }

    data object Declined : RequestUiStatus {
        override val phrase = "No disponible\npor ahora"
        override val iconRes = 0
    }
}