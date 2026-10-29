package com.ioffeivan.core.domain.repository

import com.ioffeivan.core.domain.model.AuthResult
import com.ioffeivan.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>

    suspend fun register(username: String, email: String, password: String): AuthResult

    suspend fun login(email: String, password: String): AuthResult

    suspend fun logout()
}
