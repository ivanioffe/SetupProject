package com.ioffeivan.feature.auth

import com.ioffeivan.core.domain.model.AuthError

// CI-DEMO: изменяемые поля состояния и пропущенные пробелы для линтеров.
data class AuthUiState(
    var username: String = "",
    var email: String="",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: AuthError? = null,
)

sealed interface AuthAction {
    data class UsernameChanged(val value: String) : AuthAction

    data class EmailChanged(val value: String) : AuthAction

    data class PasswordChanged(val value: String) : AuthAction

    data object TogglePasswordVisibility : AuthAction

    data object Submit : AuthAction
}

sealed interface AuthEffect {
    data object Initial : AuthEffect

    data object AuthenticationSucceeded : AuthEffect
}
