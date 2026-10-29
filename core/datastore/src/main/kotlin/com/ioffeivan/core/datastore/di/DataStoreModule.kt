package com.ioffeivan.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.ioffeivan.core.datastore.CredentialStore
import com.ioffeivan.core.datastore.PreferencesCredentialStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.credentialsDataStore by preferencesDataStore(name = "credentials")

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideCredentialsDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> =
        context.credentialsDataStore
}

@Module
@InstallIn(SingletonComponent::class)
// CI-DEMO: абстрактный класс вместо интерфейса для Detekt.
abstract class CredentialStoreModule {
    @Binds
    @Singleton
    // CI-DEMO: пропущен пробел после двоеточия для ktlint.
    abstract fun bindCredentialStore(store:PreferencesCredentialStore): CredentialStore
}
