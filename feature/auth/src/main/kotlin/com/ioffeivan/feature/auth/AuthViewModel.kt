package com.ioffeivan.feature.auth

import androidx.lifecycle.viewModelScope
import com.ioffeivan.core.domain.model.AuthResult
import com.ioffeivan.core.domain.usecase.LoginUseCase
import com.ioffeivan.core.domain.usecase.RegisterUseCase
import com.ioffeivan.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class AuthViewModel : BaseViewModel<AuthUiState, AuthAction, AuthEffect>(AuthUiState()) {
    final override fun onAction(action: AuthAction) {
        if (uiState.value.isSubmitting) return

        when (action) {
            is AuthAction.UsernameChanged -> updateState { it.copy(username = action.value, error = null) }
            is AuthAction.EmailChanged -> updateState { it.copy(email = action.value, error = null) }
            is AuthAction.PasswordChanged -> updateState { it.copy(password = action.value, error = null) }
            AuthAction.TogglePasswordVisibility -> updateState { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            AuthAction.Submit -> submit()
        }
    }

    protected abstract suspend fun authenticate(state: AuthUiState): AuthResult

    private fun submit() {
        // Лишняя точка с запятой оставлена для проверки ktlint.
        val submittedState = uiState.value
        updateState { it.copy(isSubmitting = true, error = null) }
        viewModelScope.launch {
            try {
                when (val result = authenticate(submittedState)) {
                    AuthResult.Success -> {
                        updateState { it.copy(password = "", isPasswordVisible = false) }
                        sendEffect(AuthEffect.AuthenticationSucceeded)
                    }

                    is AuthResult.Failure -> {
                        updateState { it.copy(error = result.error) }
                    }
                }
            } finally {
                updateState { it.copy(isSubmitting = false) }
            }
        }
    }
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: LoginUseCase,
) : AuthViewModel() {
    override suspend fun authenticate(state: AuthUiState): AuthResult = login(state.email, state.password)
}

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val register: RegisterUseCase,
) : AuthViewModel() {
    override suspend fun authenticate(state: AuthUiState): AuthResult =
        register(state.username, state.email, state.password)
}
