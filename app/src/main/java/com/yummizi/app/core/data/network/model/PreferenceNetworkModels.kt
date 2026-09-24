package com.yummizi.app.core.data.network.model

import com.yummizi.app.core.data.local.entities.Preference
import kotlinx.serialization.Serializable

@Serializable
data class PreferenceDto(
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val color: String,
    val createdAt: Long = System.currentTimeMillis()
)


fun Preference.toDto() = PreferenceDto(
    id = id.toString(),
    userId = userId.toString(),
    title = title,
    description = description,
    color = color,
    createdAt = createdAt
)