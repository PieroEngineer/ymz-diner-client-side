package com.yummizi.app.features.request.data.model

import com.yummizi.app.R

//sealed interface QueueStatus {
//    val phrase: String
//    val iconRes: Int
//    val name: String
//
//    data object IDLE : QueueStatus {
//        override val phrase = "Restaurant no seleccionado"
//        override val iconRes = 0 //TODO: Change the icons like this later
//        override val name = "IDLE"  //TODO: Remove this attribute and find the way to use the data object name directly
//    }
//
//    data object QUEUED : QueueStatus {
//        override val phrase = "Esperando\nrespuesta..."
//        override val iconRes = R.drawable.in_queue_icon
//        override val name = "QUEUED"
//    }
//
//    data object ACCEPTED : QueueStatus {
//        override val phrase = "Hay mesas\npara ti"
//        override val iconRes = R.drawable.enter_icon
//        override val name = "ACCEPTED"
//    }
//
//    data object PAID : QueueStatus {  /// Let user know that once they pay for one restaurant, they leave the others queues
//        override val phrase = ""
//        override val iconRes = 0
//        override val name = "PAID"
//    }
//
//    data object DECLINED : QueueStatus {
//        override val phrase = "No diponible\npor ahora"
//        override val iconRes = 0
//        override val name = "DECLINED"
//    }
//}