package com.example.yummiziapp.core.data.network.model

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