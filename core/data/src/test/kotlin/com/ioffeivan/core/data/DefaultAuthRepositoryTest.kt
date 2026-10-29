package com.ioffeivan.core.data

import com.ioffeivan.core.database.UserStore
import com.ioffeivan.core.datastore.CredentialStore
import com.ioffeivan.core.datastore.StoredCredentials
import com.ioffeivan.core.domain.model.AuthError
import com.ioffeivan.core.domain.model.AuthResult
import com.ioffeivan.core.domain.model.User
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException

class DefaultAuthRepositoryTest {
    private val users = FakeUserStore()
    private val credentials = FakeCredentialStore()
    private val repository = DefaultAuthRepository(users, credentials)

    @Test
    fun `registration persists both stores and restores the session`() =
        runTest {
            assertEquals(AuthResult.Success, repository.register("Ivan", EMAIL, PASSWORD))

            assertEquals("Ivan", users.readUsername())
            assertEquals(StoredCredentials(EMAIL, PASSWORD, isSignedIn = true), credentials.readCredentials())
            val recreatedRepository = DefaultAuthRepository(users, credentials)
            assertEquals(User("Ivan"), recreatedRepository.currentUser.first())
        }

    @Test
    fun `logout keeps credentials and incorrect password cannot sign in`() =
        runTest {
            repository.register("Ivan", EMAIL, PASSWORD)
            repository.logout()

            assertNull(repository.currentUser.first())
            assertFalse(requireNotNull(credentials.readCredentials()).isSignedIn)
            assertEquals(
                AuthResult.Failure(AuthError.INVALID_CREDENTIALS),
                repository.login(EMAIL, "wrong-password"),
            )
            assertNull(repository.currentUser.first())
            assertEquals(AuthResult.Success, repository.login(EMAIL, PASSWORD))
            assertEquals(User("Ivan"), repository.currentUser.first())
        }

    @Test
    fun `second registration does not overwrite the local account`() =
        runTest {
            repository.register("Ivan", EMAIL, PASSWORD)

            assertEquals(
                AuthResult.Failure(AuthError.ACCOUNT_ALREADY_EXISTS),
                repository.register("Other", "other@example.com", "different-password"),
            )
            assertEquals(User("Ivan"), repository.currentUser.first())
            assertEquals(EMAIL, credentials.readCredentials()?.email)
        }

    @Test
    fun `failed credentials write rolls back the username`() =
        runTest {
            credentials.writeFailure = IOException("Disk full")

            assertEquals(AuthResult.Failure(AuthError.STORAGE), repository.register("Ivan", EMAIL, PASSWORD))
            assertNull(users.readUsername())
            assertNull(repository.currentUser.first())
        }

    @Test
    fun `cancellation is propagated instead of becoming a storage error`() {
        credentials.writeFailure = CancellationException("Cancelled")

        assertThrows(CancellationException::class.java) {
            runTest {
                repository.register("Ivan", EMAIL, PASSWORD)
            }
        }

        runTest {
            assertNull(repository.currentUser.first())
        }
    }

    private companion object {
        const val EMAIL = "ivan@example.com"
        const val PASSWORD = "password123"
    }
}

private class FakeUserStore : UserStore {
    override val username = MutableStateFlow<String?>(null)

    override suspend fun readUsername() = username.value

    override suspend fun saveUsername(username: String) {
        this.username.value = username
    }

    override suspend fun deleteUsername() {
        username.value = null
    }
}

private class FakeCredentialStore : CredentialStore {
    override val credentials = MutableStateFlow<StoredCredentials?>(null)
    var writeFailure: Exception? = null

    override suspend fun readCredentials() = credentials.value

    override suspend fun saveCredentials(credentials: StoredCredentials) {
        writeFailure?.let { throw it }
        this.credentials.value = credentials
    }

    override suspend fun setSignedIn(isSignedIn: Boolean) {
        credentials.value = credentials.value?.copy(isSignedIn = isSignedIn)
    }
}
