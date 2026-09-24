package com.yummizi.app.features.auth.domain.repository

import com.yummizi.app.core.data.network.model.SignUpData
import com.yummizi.app.features.auth.domain.model.AuthResult

/**
 * Contract defining authentication operations.
 * Abstraction ensures ViewModel can be easily Unit Tested with Mocks.
 */
interface AuthRepository {
    suspend fun signUp(username: String, email: String/*, phoneNumber*/, password: String): AuthResult<Boolean>
    suspend fun signIn(email: String, password: String, newUsername: String? = null): AuthResult<Boolean>
    suspend fun checkSession(): AuthResult<Boolean>
    suspend fun signOut(): AuthResult<Unit>
}