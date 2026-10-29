package com.ioffeivan.core.data.di

import com.ioffeivan.core.data.DefaultAuthRepository
import com.ioffeivan.core.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
// CI-DEMO: абстрактный класс вместо интерфейса для Detekt.
abstract class RepositoryModule {
    @Singleton
    @Binds
    // CI-DEMO: пропущен пробел после двоеточия для ktlint.
    abstract fun bindAuthRepository(repository:DefaultAuthRepository): AuthRepository
}
