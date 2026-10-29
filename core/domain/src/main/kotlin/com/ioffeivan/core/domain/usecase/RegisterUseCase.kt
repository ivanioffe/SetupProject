package com.ioffeivan.core.domain.usecase

import com.ioffeivan.core.domain.model.AuthError
import com.ioffeivan.core.domain.model.AuthResult
import com.ioffeivan.core.domain.repository.AuthRepository
import javax.inject.Inject

const val USERNAME_MAX_LENGTH = 40
const val PASSWORD_MIN_LENGTH = 8
const val PASSWORD_MAX_LENGTH = 128

class RegisterUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(username: String, email: String, password: String): AuthResult {
        val normalizedUsername = username.trim()
        val normalizedEmail = email.trim().lowercase()
        return when {
            normalizedUsername.isEmpty() || normalizedUsername.length > USERNAME_MAX_LENGTH -> {
                AuthResult.Failure(AuthError.INVALID_USERNAME)
            }

            !AuthValidation.isEmailValid(normalizedEmail) -> {
                AuthResult.Failure(AuthError.INVALID_EMAIL)
            }

            password.length !in PASSWORD_MIN_LENGTH..PASSWORD_MAX_LENGTH -> {
                AuthResult.Failure(AuthError.INVALID_PASSWORD)
            }

            else -> {
                repository.register(normalizedUsername, normalizedEmail, password)
            }
        }
    }
}
