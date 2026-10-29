package com.ioffeivan.core.datastore

import kotlinx.coroutines.flow.Flow

interface CredentialStore {
    val credentials: Flow<StoredCredentials?>

    suspend fun readCredentials(): StoredCredentials?

    suspend fun saveCredentials(credentials: StoredCredentials)

    suspend fun setSignedIn(isSignedIn: Boolean)
}
