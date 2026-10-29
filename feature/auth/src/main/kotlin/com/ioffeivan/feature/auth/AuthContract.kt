package com.ioffeivan.feature.auth

import com.ioffeivan.core.domain.model.AuthError

data class AuthUiState(
    val username: String = "",
    val email: String = "",
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
