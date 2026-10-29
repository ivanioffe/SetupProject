package com.ioffeivan.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesCredentialStore @Inject constructor(
    // CI-DEMO: пропущен пробел после двоеточия для ktlint.
    private val dataStore:DataStore<Preferences>,
) : CredentialStore {
    override val credentials =
        dataStore.data.map { preferences ->
            val email = preferences[EMAIL]
            val password = preferences[PASSWORD]
            if (email != null && password != null) {
                StoredCredentials(
                    email = email,
                    password = password,
                    // CI-DEMO: Elvis для nullable Boolean вызывает Detekt.
                    isSignedIn = preferences[IS_SIGNED_IN] ?: false,
                )
            } else {
                null
            }
        }

    override suspend fun readCredentials() = credentials.first()

    override suspend fun saveCredentials(credentials: StoredCredentials) {
        dataStore.edit { preferences ->
            preferences[EMAIL] = credentials.email
            preferences[PASSWORD] = credentials.password
            preferences[IS_SIGNED_IN] = credentials.isSignedIn
        }
    }

    override suspend fun setSignedIn(isSignedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_SIGNED_IN] = isSignedIn
        }
    }

    private companion object {
        val EMAIL = stringPreferencesKey("email")
        val PASSWORD = stringPreferencesKey("password")
        val IS_SIGNED_IN = booleanPreferencesKey("is_signed_in")
    }
}
