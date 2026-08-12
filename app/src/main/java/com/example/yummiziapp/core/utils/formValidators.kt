package com.example.yummiziapp.core.utils

private val EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
fun String.isEmailValid(): Boolean {
    return EMAIL_REGEX.matches(this)
}

fun String.isPasswordValid(): Boolean {
    return this.length > 5
}

fun String.isUsernameValid(): Boolean {
    return this.length > 3
}

fun isNotEmpty(vararg fields: String): Boolean {
    return fields.all { it.isNotBlank() }
}
