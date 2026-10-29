package com.ioffeivan.core.domain.model

sealed interface AuthResult {
    data object Success : AuthResult

    data class Failure(val error: AuthError) : AuthResult
}
