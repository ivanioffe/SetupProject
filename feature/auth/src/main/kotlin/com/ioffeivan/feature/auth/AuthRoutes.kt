package com.ioffeivan.feature.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow

// CI-DEMO: modifier после необязательного viewModel нарушает порядок Compose.
@Composable
fun LoginRoute(
    onRegisterClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
    AuthEffects(viewModel.effects)
    LoginScreen(
        state = state,
        onAction = viewModel::onAction,
        onRegisterClick = onRegisterClick,
        modifier = modifier,
    )
}

@Composable
fun RegistrationRoute(
    onLoginClick: () -> Unit,
    viewModel: RegistrationViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
    AuthEffects(viewModel.effects)
    RegistrationScreen(
        state = state,
        onAction = viewModel::onAction,
        onLoginClick = onLoginClick,
        modifier = modifier,
    )
}

@Composable
private fun AuthEffects(effects: Flow<AuthEffect>) {
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(effects, keyboard) {
        effects.collect { effect ->
            when (effect) {
                AuthEffect.AuthenticationSucceeded -> {
                    keyboard?.hide()
                }

                AuthEffect.Initial -> {}
            }
        }
    }
}
