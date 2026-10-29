package com.ioffeivan.feature.auth

import com.ioffeivan.core.domain.model.AuthError
import com.ioffeivan.core.domain.model.AuthResult
import com.ioffeivan.core.domain.model.User
import com.ioffeivan.core.domain.repository.AuthRepository
import com.ioffeivan.core.domain.usecase.LoginUseCase
import com.ioffeivan.core.domain.usecase.RegisterUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login blocks duplicate submissions and clears password on success`() =
        runTest {
            val repository = FakeAuthRepository()
            val viewModel = LoginViewModel(LoginUseCase(repository))
            viewModel.onAction(AuthAction.EmailChanged("ivan@example.com"))
            viewModel.onAction(AuthAction.PasswordChanged("password123"))
            viewModel.onAction(AuthAction.Submit)
            viewModel.onAction(AuthAction.Submit)
            assertTrue(viewModel.uiState.value.isSubmitting)

            repository.result.complete(AuthResult.Success)
            advanceUntilIdle()

            assertEquals(1, repository.loginCalls)
            assertFalse(viewModel.uiState.value.isSubmitting)
            assertEquals("", viewModel.uiState.value.password)
            assertEquals(AuthEffect.AuthenticationSucceeded, viewModel.effects.first())
        }

    @Test
    fun `registration reports invalid username without calling storage`() =
        runTest {
            val repository = FakeAuthRepository()
            val viewModel = RegistrationViewModel(RegisterUseCase(repository))
            viewModel.onAction(AuthAction.EmailChanged("ivan@example.com"))
            viewModel.onAction(AuthAction.PasswordChanged("password123"))
            viewModel.onAction(AuthAction.Submit)
            advanceUntilIdle()

            assertEquals(AuthError.INVALID_USERNAME, viewModel.uiState.value.error)
            assertFalse(viewModel.uiState.value.isSubmitting)
            assertEquals(0, repository.registrationCalls)

            viewModel.onAction(AuthAction.UsernameChanged("Иван"))
            assertEquals(null, viewModel.uiState.value.error)
        }

    private class FakeAuthRepository : AuthRepository {
        override val currentUser: Flow<User?> = flowOf(null)
        val result = CompletableDeferred<AuthResult>()
        var loginCalls = 0
            private set
        var registrationCalls = 0
            private set

        override suspend fun login(email: String, password: String): AuthResult {
            loginCalls++
            return result.await()
        }

        override suspend fun register(
            username: String,
            email: String,
            password: String,
        ): AuthResult {
            registrationCalls++
            return result.await()
        }

        override suspend fun logout() = Unit
    }
}
