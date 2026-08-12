package com.example.yummiziapp.features.auth.domain.repository

import com.example.yummiziapp.core.data.network.model.SignUpData
import com.example.yummiziapp.features.auth.domain.model.AuthResult

/**
 * Contract defining authentication operations.
 * Abstraction ensures ViewModel can be easily Unit Tested with Mocks.
 */
interface AuthRepository {
    suspend fun signUp(signUpData: SignUpData): AuthResult<Boolean>
    suspend fun signIn(email: String, password: String): AuthResult<Boolean>
    suspend fun checkSession(): AuthResult<Boolean>
    suspend fun signOut(): AuthResult<Unit>
}