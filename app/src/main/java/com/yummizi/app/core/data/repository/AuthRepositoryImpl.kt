package com.yummizi.app.core.data.repository

import android.util.Log
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.yummizi.app.core.data.network.model.SignUpData
import com.yummizi.app.features.auth.domain.model.AuthResult
import com.yummizi.app.features.auth.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import com.amplifyframework.kotlin.core.Amplify
import com.yummizi.app.core.data.di.IoDispatcher
import com.yummizi.app.core.data.local.dao.LoggedUserDao
import com.yummizi.app.core.data.local.entities.LoggedUser
import com.yummizi.app.core.data.network.api.UserApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val loggedUserDao: LoggedUserDao,
    private val api: UserApi,

    @IoDispatcher private val ioDispatcher: CoroutineDispatcher

) : AuthRepository {

    /**
     * Executes Cognito Sign-Up with custom user attributes.
     * DIDACTIC NOTE:
     * AWS Cognito uses attribute keys for Standard Attributes (Email, Phone).
     * The `username` parameter acts as the primary record identifier in the pool.
     */
    override suspend fun signUp( username: String, email: String/*, phoneNumber*/, password: String): AuthResult<Boolean> = withContext(ioDispatcher) {
        val signUpData = SignUpData(
//            username = username,
            email = email,
            password = password
        )

        try {
            val options = AuthSignUpOptions.builder()
//                .userAttribute(AuthUserAttributeKey.email(), signUpData.email)
//                .userAttribute(AuthUserAttributeKey.phoneNumber(), signUpData.phoneNumber)
                .build()

            val result = Amplify.Auth.signUp(
//                signUpData.username,
                signUpData.email,
                signUpData.password,
                options
            )

            // Returns true if sign up is complete, false if user pool requires confirmation/OTP
            AuthResult.Success(result.isSignUpComplete)
        } catch (e: AuthException) {
            AuthResult.Error(e, e.message ?: "Sign up failed due to an unknown error")
        }
    }

    /**
     * Authenticates user against Cognito User Pool.
     * DIDACTIC NOTE:
     * Password credentials are securely transmuted using SRP (Secure Remote Password)
     * protocol under the hood by Amplify SDK—passwords are never sent in plain text over the network.
     */
    override suspend fun signIn(email: String, password: String, newUsername: String?): AuthResult<Boolean> = withContext(ioDispatcher) {

        try {
            val result = Amplify.Auth.signIn(email, password)

            Log.d("MainActivity" ,"🔎🪪  Sign in was successful (inside)")

            val user = Amplify.Auth.getCurrentUser()

            Log.d("MainActivity" ,"🔎🪪  User: $user\nAnd the username direct is: ${newUsername}")

            val username = newUsername ?: api.getUserData(user.userId).username

            Log.d("MainActivity" ,"🔎🪪   Collected Username: $username")

            loggedUserDao.emptyUserTable()

            loggedUserDao.saveUser(
                LoggedUser(
                    id = Uuid.parse(user.userId),
                    name = username,
                    favoriteRestaurants = emptyList(),
                    companySize = 0,
                    specifics = emptyList()
                )
            )

            AuthResult.Success(result.isSignedIn)
        } catch (e: AuthException) {
            Log.d("MainActivity" ,"🔎🪪  It's an auth error : ${e.message}")
            AuthResult.Error(e, e.message ?: "Authentication failed")
        } catch (e: Exception) {
            Log.d("MainActivity" ,"🔎🪪  It's default error : ${e.message}")
            AuthResult.Error(e, e.message ?: "An unexpected error occurred during sign in")
        }
    }

    /**
     * Verifies if local tokens are valid and refreshes them silently if expired.
     */
    override suspend fun checkSession(): AuthResult<Boolean> {
        return try {
            val session = Amplify.Auth.fetchAuthSession()
            AuthResult.Success(session.isSignedIn)

        } catch (e: AuthException) {
            AuthResult.Error(e, e.message ?: "Session check failed")
        }
    }

    override suspend fun signOut(): AuthResult<Unit> {  // TODO: Empty all tables
        return try {
            Amplify.Auth.signOut()
            AuthResult.Success(Unit)
        } catch (e: AuthException) {
            AuthResult.Error(e)
        }
    }
}