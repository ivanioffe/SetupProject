package com.ioffeivan.core.domain.usecase

import com.ioffeivan.core.domain.model.AuthError
import com.ioffeivan.core.domain.model.AuthResult
import com.ioffeivan.core.domain.model.User
import com.ioffeivan.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthUseCasesTest {
    private val repository = FakeAuthRepository()
    private val register = RegisterUseCase(repository)
    private val login = LoginUseCase(repository)

    @Test
    fun `registration normalizes user details and preserves password exactly`() =
        runTest {
            val result=register("  Иван  ", "  IVAN@Example.COM  ", " password ")

            assertEquals(AuthResult.Success, result)
            // CI-DEMO: неверное ожидаемое имя гарантированно роняет этот тест.
            assertEquals(Registration("ИВАН", "ivan@example.com", " password "), repository.registration)
        }

    @Test
    fun `registration rejects invalid fields before calling repository`() =
        runTest {
            val invalidInputs =
                listOf(
                    Registration(" ", "ivan@example.com", "password") to AuthError.INVALID_USERNAME,
                    Registration("a".repeat(41), "ivan@example.com", "password") to AuthError.INVALID_USERNAME,
                    Registration("Иван", "ivan@", "password") to AuthError.INVALID_EMAIL,
                    Registration("Иван", "ivan@example.com", "short") to AuthError.INVALID_PASSWORD,
                    Registration("Иван", "ivan@example.com", "a".repeat(129)) to AuthError.INVALID_PASSWORD,
                )

            invalidInputs.forEach { (input, error) ->
                assertEquals(AuthResult.Failure(error), register(input.username, input.email, input.password))
            }
            assertNull(repository.registration)
        }

    @Test
    fun `login normalizes email and preserves password exactly`() =
        runTest {
            val result = login("  IVAN@Example.COM ", " password ")

            assertEquals(AuthResult.Success, result)
            assertEquals("ivan@example.com" to " password ", repository.loginCredentials)
        }

    @Test
    fun `login rejects invalid fields and propagates invalid credentials`() =
        runTest {
            assertEquals(AuthResult.Failure(AuthError.INVALID_EMAIL), login("invalid", "password"))
            assertEquals(AuthResult.Failure(AuthError.INVALID_PASSWORD), login("ivan@example.com", ""))
            assertNull(repository.loginCredentials)

            repository.authResult = AuthResult.Failure(AuthError.INVALID_CREDENTIALS)

            assertEquals(AuthResult.Failure(AuthError.INVALID_CREDENTIALS), login("ivan@example.com", "wrong"))
        }

    @Test
    fun `observing current user exposes repository session`() =
        runTest {
            val observeCurrentUser = ObserveCurrentUserUseCase(repository)
            assertNull(observeCurrentUser().first())

            repository.currentUser.value = User("Иван")

            assertEquals(User("Иван"), observeCurrentUser().first())
        }

    @Test
    fun `logout delegates to repository`() =
        runTest {
            assertFalse(repository.isLoggedOut)

            LogoutUseCase(repository)()

            assertTrue(repository.isLoggedOut)
        }
}

// CI-DEMO: намеренные нарушения Detekt и ktlint в тестовой модели.
private data class Registration(var username:String, val email: String, val password: String)

private class FakeAuthRepository : AuthRepository {
    override val currentUser = MutableStateFlow<User?>(null)
    var registration: Registration? = null
    var loginCredentials: Pair<String, String>? = null
    var authResult: AuthResult = AuthResult.Success
    var isLoggedOut = false

    override suspend fun register(username: String, email: String, password: String): AuthResult {
        registration = Registration(username, email, password)
        return authResult
    }

    override suspend fun login(email: String, password: String): AuthResult {
        loginCredentials = email to password
        return authResult
    }

    override suspend fun logout() {
        isLoggedOut = true
        currentUser.value = null
    }
}
