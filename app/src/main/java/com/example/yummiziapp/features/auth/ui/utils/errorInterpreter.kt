package com.example.yummiziapp.features.auth.ui.utils

import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.exceptions.service.InvalidPasswordException
import com.amplifyframework.auth.cognito.exceptions.service.UsernameExistsException
import com.amplifyframework.auth.cognito.exceptions.service.InvalidParameterException
import com.amplifyframework.auth.cognito.exceptions.service.CodeDeliveryFailureException
import com.amplifyframework.auth.cognito.exceptions.service.LimitExceededException
import com.amplifyframework.auth.cognito.exceptions.service.TooManyRequestsException

fun getAuthErrorMessage(error: Throwable?): String {
    if (error == null) return "Ocurrió un error desconocido durante el registro."

    if (error is AuthException) {
        // Evaluamos la causa raíz que proviene directamente de AWS Cognito
        when (error.cause) {
            is UsernameExistsException ->
                return "Ya existe una cuenta con este correo electrónico."
            is InvalidPasswordException ->
                return "La contraseña no cumple con los requisitos de seguridad. Asegúrate de incluir números y caracteres especiales."
            is InvalidParameterException ->
                return "Los datos ingresados no son válidos. Por favor, verifica el formato de tu correo y teléfono."
            is CodeDeliveryFailureException ->
                return "No se pudo enviar el correo de verificación. Por favor, revisa bien tu dirección de correo electrónico."
            is TooManyRequestsException, is LimitExceededException ->
                return "Demasiados intentos de registro. Por favor, espera unos minutos antes de intentarlo de nuevo."
        }
        // Si es una excepción de Amplify pero no coincide con las anteriores
        return error.message ?: "Ocurrió un error de autenticación inesperado."
    }

    // Para cualquier otro error genérico de la plataforma/red
    return error.message ?: "Ocurrió un error inesperado."
}
