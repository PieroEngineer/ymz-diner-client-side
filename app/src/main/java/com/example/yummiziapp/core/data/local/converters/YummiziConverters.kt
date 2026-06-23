package com.example.yummiziapp.core.data.local.converters

import androidx.room.TypeConverter
import kotlin.uuid.Uuid
import kotlinx.serialization.encodeToString
import com.example.yummiziapp.features.request.data.model.QueueStatus
import kotlinx.serialization.json.Json

class YummiziConverters {

    // Centralized JSON instance optimized for modern standard collections and native types
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true // Useful for handling nulls as defaults
        // 🌟 Modern Upgrade: No manual contextual Uuid module needed anymore!
    }

    // --- String Lists (For Restaurant Images/Menus) ---
    @TypeConverter
    fun fromStringList(value: List<String>): String = json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = json.decodeFromString(value)

    // --- UUID Lists (For LoggedUser State Lists) ---
    @TypeConverter
    fun fromUuidList(value: List<Uuid>): String = json.encodeToString(value)

    @TypeConverter
    fun toUuidList(value: String): List<Uuid> = json.decodeFromString(value)

    // --- Single UUID (For Primary Keys/IDs) ---
    @TypeConverter
    fun fromUuid(uuid: Uuid?): String? = uuid?.toString()

    @TypeConverter
    fun toUuid(value: String?): Uuid? = value?.let { Uuid.parse(it) }

    // --- Enums ---
    @TypeConverter
    fun fromQueueStatus(status: QueueStatus): String = status.name

    @TypeConverter
    fun toQueueStatus(value: String): QueueStatus {
        // 💡 Pro-Tip: You can use runCatching to automatically handle unexpected string entries dynamically,
        // but keeping your clean fallback strategy is perfect for database resilience:
        return when (value) {
            "QUEUED" -> QueueStatus.QUEUED
            "ACCEPTED" -> QueueStatus.ACCEPTED
            "DECLINED" -> QueueStatus.DECLINED
            else -> QueueStatus.IDLE // Default fallback
        }
    }
}