package com.yummizi.app.features.restaurant.domain.model

import com.yummizi.app.core.data.local.entities.Request

data class SyncRequestContent(
    val toInsert: List<Request>,
    val toUpdate: List<Request>,
    val toDeleteIds: List<String>,
)
