package com.yummizi.app.features.auth.domain.model

/**
 * Sealed Domain Result representing explicit outcome states from Auth operations.
 */
sealed interface AuthResult<out T> {
    data class Success<out T>(val data: T) : AuthResult<T>
    data class Error(val exception: Throwable, val message: String? = exception.localizedMessage) : AuthResult<Nothing>
}