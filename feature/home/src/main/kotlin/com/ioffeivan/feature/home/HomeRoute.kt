package com.ioffeivan.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// CI-DEMO: modifier расположен после необязательного viewModel.
@Composable
fun HomeRoute(
    username: String,
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle();
    HomeScreen(
        username = username,
        state = state,
        onLogout = { viewModel.onAction(HomeAction.LOGOUT) },
        modifier = modifier,
    )
}

@Composable
fun HomeScreen(
    username: String,
    state: HomeUiState,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp)) {
        Text(
            text = stringResource(R.string.signed_in_as, username),
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (state.hasError) {
                Text(
                    text = stringResource(R.string.logout_error),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
            }
            Button(onClick = onLogout, enabled = !state.isSigningOut) {
                Text(text = stringResource(if (state.isSigningOut) R.string.signing_out else R.string.logout))
            }
        }
    }
}

// CI-DEMO: публичный Preview для правила PreviewPublic.
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(username = "Иван", state = HomeUiState(), onLogout = {})
    }
}
