package com.ioffeivan.core.domain.usecase

internal object AuthValidation {
    const val MAX_USERNAME_LENGTH = 40
    const val MIN_PASSWORD_LENGTH = 8
    const val MAX_PASSWORD_LENGTH = 128

    private val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isEmailValid(email: String): Boolean = emailPattern.matches(email)
}
