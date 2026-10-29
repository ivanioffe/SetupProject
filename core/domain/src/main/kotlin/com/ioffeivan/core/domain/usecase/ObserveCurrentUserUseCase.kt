package com.ioffeivan.core.domain.usecase

import com.ioffeivan.core.domain.model.User
import com.ioffeivan.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCurrentUserUseCase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke(): Flow<User?> = repository.currentUser
}
