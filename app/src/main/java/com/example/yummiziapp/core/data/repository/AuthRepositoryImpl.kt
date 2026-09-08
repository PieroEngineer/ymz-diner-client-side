package com.example.yummiziapp.core.data.repository

import com.amplifyframework.auth.options.AuthSignUpOptions
import com.example.yummiziapp.core.data.network.model.SignUpData
import com.example.yummiziapp.features.auth.domain.model.AuthResult
import com.example.yummiziapp.features.auth.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import com.amplifyframework.kotlin.core.Amplify
import com.example.yummiziapp.core.data.di.IoDispatcher
import com.example.yummiziapp.core.data.local.dao.LoggedUserDao
import com.example.yummiziapp.core.data.local.entities.LoggedUser
import com.example.yummiziapp.core.data.network.api.UserApi
import com.example.yummiziapp.core.data.network.model.UserCreationPayload
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

            val user = Amplify.Auth.getCurrentUser()
            val userId = user.userId

            // TODO: Handle the situation that Cognito register the user but dynamoDB fails

            // ️Upload the user data to the database
            api.createUser(
                userId = userId,    // TODO: Research if the sending of the user ID should be here (like now) or in the backend
                UserCreationPayload(
                    username = username
                )
            )

            val newUser = LoggedUser(
                id = Uuid.parse(userId),
                name = username,
                favoriteRestaurants = emptyList(),
                companySize = 0,
                specifics = emptyList()
            )

            loggedUserDao.saveUser(newUser)

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

            val user = Amplify.Auth.getCurrentUser()

            val remoteUserData = api.getUserData(user.userId)

            loggedUserDao.emptyUserTable()

            loggedUserDao.saveUser(
                LoggedUser(
                    id = Uuid.parse(user.userId),
                    name = remoteUserData.username,
                    favoriteRestaurants = emptyList(),
                    companySize = 0,
                    specifics = emptyList()
                )
            )

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

    override suspend fun signOut(): AuthResult<Unit> {  // TODO: Empty all tables
        return try {
            Amplify.Auth.signOut()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }
}