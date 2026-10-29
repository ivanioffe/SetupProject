package com.ioffeivan.core.data

import android.database.SQLException
import com.ioffeivan.core.database.UserStore
import com.ioffeivan.core.datastore.CredentialStore
import com.ioffeivan.core.datastore.StoredCredentials
import com.ioffeivan.core.domain.model.AuthError
import com.ioffeivan.core.domain.model.AuthResult
import com.ioffeivan.core.domain.model.User
import com.ioffeivan.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val userStore: UserStore,
    private val credentialStore: CredentialStore,
) : AuthRepository {
    private val mutationMutex = Mutex()

    override val currentUser =
        combine(userStore.username, credentialStore.credentials) { username, credentials ->
            if (credentials?.isSignedIn == true && username != null) User(username) else null
        }.distinctUntilChanged()

    override suspend fun register(username: String, email: String, password: String): AuthResult =
        mutationMutex.withLock {
            storageResult {
                if (credentialStore.readCredentials() != null) {
                    return@storageResult AuthResult.Failure(AuthError.ACCOUNT_ALREADY_EXISTS)
                }
                userStore.saveUsername(username)
                saveNewCredentials(StoredCredentials(email, password, isSignedIn = true))
                AuthResult.Success
            }
        }

    override suspend fun login(email: String, password: String): AuthResult =
        mutationMutex.withLock {
            storageResult {
                val credentials = credentialStore.readCredentials()
                if (credentials == null || credentials.email != email || credentials.password != password) {
                    return@storageResult AuthResult.Failure(AuthError.INVALID_CREDENTIALS)
                }
                if (userStore.readUsername() == null) {
                    return@storageResult AuthResult.Failure(AuthError.STORAGE)
                }
                credentialStore.setSignedIn(isSignedIn = true)
                AuthResult.Success
            }
        }

    override suspend fun logout() {
        mutationMutex.withLock {
            credentialStore.setSignedIn(isSignedIn = false)
        }
    }

    private suspend fun saveNewCredentials(credentials: StoredCredentials) {
        try {
            credentialStore.saveCredentials(credentials)
        } catch (exception: IOException) {
            // An unfinished registration must not leave a usable profile behind.
            userStore.deleteUsername()
            throw exception
        }
    }

    private suspend fun storageResult(block: suspend () -> AuthResult): AuthResult =
        try {
            block()
        } catch (_: IOException) {
            AuthResult.Failure(AuthError.STORAGE)
        } catch (_: SQLException) {
            AuthResult.Failure(AuthError.STORAGE)
        }
}
