package com.example.yummiziapp.core.data.network.model

/**
 * Data Transfer Object (DTO) capturing user inputs for registration.
 * Keeping this in Domain decouples UI from specific backend payloads.
 */
data class SignUpData(
    val username: String,
    val email: String,
//TODO:    val phoneNumber: String, /// Must follow E.164 format (e.g., +11234567890) for Cognito
    val password: String
)