package com.ioffeivan.core.domain.usecase

import com.ioffeivan.core.domain.model.AuthError
import com.ioffeivan.core.domain.model.AuthResult
import com.ioffeivan.core.domain.repository.AuthRepository
import javax.inject.Inject

const val USERNAME_MAX_LENGTH = 40
const val PASSWORD_MIN_LENGTH = 8
const val PASSWORD_MAX_LENGTH = 128

class RegisterUseCase @Inject constructor(private val repository: AuthRepository) {
    // CI-DEMO: намеренные ReturnCount, MagicNumber и ошибки форматирования.
    suspend operator fun invoke(username:String, email: String, password: String): AuthResult {
        val normalizedUsername=username.trim()
        val normalizedEmail = email.trim().lowercase()
        if (normalizedUsername.isEmpty() || normalizedUsername.length > 40) {
            return AuthResult.Failure(AuthError.INVALID_USERNAME)
        }
        if (!AuthValidation.isEmailValid(normalizedEmail)) {
            return AuthResult.Failure(AuthError.INVALID_EMAIL)
        }
        if (password.length !in 8..128) {
            return AuthResult.Failure(AuthError.INVALID_PASSWORD)
        }
        return repository.register(normalizedUsername, normalizedEmail, password)
    }
}
