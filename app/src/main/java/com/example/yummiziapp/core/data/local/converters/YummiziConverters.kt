package com.example.yummiziapp.core.data.local.converters

import androidx.room.TypeConverter
import com.example.yummiziapp.core.data.local.model.AvailableSpecificsToken
import com.example.yummiziapp.core.data.local.model.RequestStatusToken
import kotlin.uuid.Uuid
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Instant

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
    fun fromRequestStatusToken(value: RequestStatusToken): String = value.name

    @TypeConverter
    fun toRequestStatusToken(value: String): RequestStatusToken = RequestStatusToken.valueOf(value)

    // --- AvailableSpecificsToken Lists ---
    @TypeConverter
    fun fromAvailableSpecificsTokenList(value: List<AvailableSpecificsToken>): String = json.encodeToString(value)

    @TypeConverter
    fun toAvailableSpecificsTokenList(value: String): List<AvailableSpecificsToken> = json.decodeFromString(value)

    // --- Instants ---

    @TypeConverter
    fun fromEpochMilliseconds(value: Long?): Instant? {
        return value?.let { Instant.fromEpochMilliseconds(it) }
    }

    @TypeConverter
    fun toEpochMilliseconds(instant: Instant?): Long? {
        return instant?.toEpochMilliseconds()
    }

}