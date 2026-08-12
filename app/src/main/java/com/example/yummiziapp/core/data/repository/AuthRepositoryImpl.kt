package com.example.yummiziapp.core.data.repository

import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.example.yummiziapp.core.data.network.model.SignUpData
import com.example.yummiziapp.features.auth.domain.model.AuthResult
import com.example.yummiziapp.features.auth.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import com.amplifyframework.kotlin.core.Amplify

@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    /**
     * Executes Cognito Sign-Up with custom user attributes.
     * DIDACTIC NOTE:
     * AWS Cognito uses attribute keys for Standard Attributes (Email, Phone).
     * The `username` parameter acts as the primary record identifier in the pool.
     */
    override suspend fun signUp(signUpData: SignUpData): AuthResult<Boolean> {
        return try {
            val options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.email(), signUpData.email)
                .userAttribute(AuthUserAttributeKey.phoneNumber(), signUpData.phoneNumber)
                .build()

            val result = Amplify.Auth.signUp(
                signUpData.username,
                signUpData.password,
                options
            )

            // Returns true if sign up is complete, false if user pool requires confirmation/OTP
            AuthResult.Success(result.isSignUpComplete)
        } catch (e: Exception) {
            AuthResult.Error(e, e.message ?: "Sign up failed due to an unknown error")
        }
    }

    /**
     * Authenticates user against Cognito User Pool.
     * DIDACTIC NOTE:
     * Password credentials are securely transmuted using SRP (Secure Remote Password)
     * protocol under the hood by Amplify SDK—passwords are never sent in plain text over the network.
     */
    override suspend fun signIn(email: String, password: String): AuthResult<Boolean> {
        return try {
            val result = Amplify.Auth.signIn(email, password)
            AuthResult.Success(result.isSignedIn)
        } catch (e: Exception) {
            AuthResult.Error(e, e.message ?: "Authentication failed")
        }
    }

    /**
     * Verifies if local tokens are valid and refreshes them silently if expired.
     */
    override suspend fun checkSession(): AuthResult<Boolean> {
        return try {
            val session = Amplify.Auth.fetchAuthSession()
            AuthResult.Success(session.isSignedIn)

        } catch (e: Exception) {
            AuthResult.Error(e, e.message ?: "Session check failed")
        }
    }

    override suspend fun signOut(): AuthResult<Unit> {
        return try {
            Amplify.Auth.signOut()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }
}