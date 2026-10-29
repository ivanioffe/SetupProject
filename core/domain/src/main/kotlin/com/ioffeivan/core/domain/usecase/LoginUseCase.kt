package com.ioffeivan.core.domain.usecase

import com.ioffeivan.core.domain.model.AuthError
import com.ioffeivan.core.domain.model.AuthResult
import com.ioffeivan.core.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        val normalizedEmail = email.trim().lowercase()
        return when {
            !AuthValidation.isEmailValid(normalizedEmail) -> AuthResult.Failure(AuthError.INVALID_EMAIL)
            password.isEmpty() -> AuthResult.Failure(AuthError.INVALID_PASSWORD)
            else -> repository.login(normalizedEmail, password)
        }
    }
}
