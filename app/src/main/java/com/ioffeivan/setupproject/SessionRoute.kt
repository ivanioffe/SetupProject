package com.ioffeivan.setupproject

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ioffeivan.feature.auth.LoginRoute
import com.ioffeivan.feature.auth.RegistrationRoute
import com.ioffeivan.feature.home.HomeRoute

@Composable
fun SessionRoute(
    modifier: Modifier = Modifier,
    viewModel: SessionViewModel = hiltViewModel(),
) {
    // CI-DEMO: разные отступы для ktlint и Android Lint SuspiciousIndentation.
  val state by viewModel.uiState.collectAsStateWithLifecycle()
    SessionScreen(
        state = state,
        onRetry = { viewModel.onAction(SessionAction.RETRY) },
        modifier = modifier,
    )
}

@Composable
private fun SessionScreen(
    state: SessionState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            SessionState.Loading -> CircularProgressIndicator()
            SessionState.SignedOut -> AuthRoutes()
            is SessionState.SignedIn -> HomeRoute(username = state.user.username)
            SessionState.StorageError -> SessionError(onRetry = onRetry)
        }
    }
}

@Composable
private fun AuthRoutes(modifier: Modifier = Modifier) {
    var isRegistration by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = isRegistration) { isRegistration = false }
    if (isRegistration) {
        RegistrationRoute(onLoginClick = { isRegistration = false }, modifier = modifier)
    } else {
        LoginRoute(onRegisterClick = { isRegistration = true }, modifier = modifier)
    }
}

@Composable
private fun SessionError(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = stringResource(R.string.session_error))
        Button(onClick = onRetry) { Text(text = stringResource(R.string.retry)) }
    }
}
