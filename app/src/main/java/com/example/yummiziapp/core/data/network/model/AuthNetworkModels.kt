package com.example.yummiziapp.core.data.network.model

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) capturing user inputs for registration.
 * Keeping this in Domain decouples UI from specific backend payloads.
 */
data class SignUpData(  /// @Serializable?
//    val username: String,
    val email: String,
//TODO:    val phoneNumber: String, /// Must follow E.164 format (e.g., +11234567890) for Cognito
    val password: String
)

@Serializable
data class UserCreationPayload(
    val username: String,
)

@Serializable
data class UserCreationResponseDto(
    val message: String
)

@Serializable
data class UserDataPayload( // TODO: Move it to User network models
    val username: String,
)