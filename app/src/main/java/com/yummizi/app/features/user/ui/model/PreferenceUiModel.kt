package com.yummizi.app.features.user.ui.model

import androidx.compose.ui.graphics.Color
import kotlin.uuid.Uuid

data class PreferenceUiModel(
    val id: Uuid,
    val title: String,
    val descriptionPart: String = "--",
    val color: Color,
    val isSyncing: Boolean
)