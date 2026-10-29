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
interface RepositoryModule {
    @Singleton
    @Binds
    fun bindAuthRepository(repository: DefaultAuthRepository): AuthRepository
}
