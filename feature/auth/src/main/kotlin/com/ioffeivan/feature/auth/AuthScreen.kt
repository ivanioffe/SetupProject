package com.ioffeivan.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ioffeivan.core.domain.model.AuthError

@Composable
fun LoginScreen(
    state: AuthUiState,
    onAction: (AuthAction) -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AuthScreen(
        state = state,
        isRegistration = false,
        onAction = onAction,
        onSwitchScreen = onRegisterClick,
        modifier = modifier,
    )
}

@Composable
fun RegistrationScreen(
    state: AuthUiState,
    onAction: (AuthAction) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AuthScreen(
        state = state,
        isRegistration = true,
        onAction = onAction,
        onSwitchScreen = onLoginClick,
        modifier = modifier,
    )
}

@Composable
private fun AuthScreen(
    state: AuthUiState,
    isRegistration: Boolean,
    onAction: (AuthAction) -> Unit,
    onSwitchScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier =
                Modifier
                    .widthIn(max = 440.dp)
                    .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text =
                    stringResource(
                        if (isRegistration) R.string.auth_registration_title else R.string.auth_login_title,
                    ),
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                text =
                    stringResource(
                        if (isRegistration) R.string.auth_registration_description else R.string.auth_login_description,
                    ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            AuthFields(state = state, isRegistration = isRegistration, onAction = onAction)
            state.error?.let { error ->
                Text(
                    text = authErrorText(error),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Button(
                onClick = {
                    onAction(AuthAction.Submit)
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                enabled = !state.isSubmitting,
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(if (isRegistration) R.string.auth_register else R.string.auth_login))
                }
            }
            TextButton(
                onClick = onSwitchScreen,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSubmitting,
            ) {
                Text(
                    stringResource(
                        if (isRegistration) R.string.auth_already_registered else R.string.auth_create_account,
                    ),
                )
            }
        }
    }
}

@Composable
private fun AuthFields(
    state: AuthUiState,
    isRegistration: Boolean,
    onAction: (AuthAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (isRegistration) {
            OutlinedTextField(
                value = state.username,
                onValueChange = { onAction(AuthAction.UsernameChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSubmitting,
                label = { Text(stringResource(R.string.auth_username)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            )
        }
        OutlinedTextField(
            value = state.email,
            onValueChange = { onAction(AuthAction.EmailChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSubmitting,
            label = { Text(stringResource(R.string.auth_email)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        )
        PasswordField(state = state, isRegistration = isRegistration, onAction = onAction)
    }
}

@Composable
private fun PasswordField(
    state: AuthUiState,
    isRegistration: Boolean,
    onAction: (AuthAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = state.password,
        onValueChange = { onAction(AuthAction.PasswordChanged(it)) },
        modifier = modifier.fillMaxWidth(),
        enabled = !state.isSubmitting,
        label = { Text(stringResource(R.string.auth_password)) },
        supportingText =
            if (isRegistration) {
                { Text(stringResource(R.string.auth_password_hint)) }
            } else {
                null
            },
        trailingIcon = {
            TextButton(
                onClick = { onAction(AuthAction.TogglePasswordVisibility) },
                enabled = !state.isSubmitting,
            ) {
                Text(
                    stringResource(
                        if (state.isPasswordVisible) R.string.auth_hide_password else R.string.auth_show_password,
                    ),
                )
            }
        },
        singleLine = true,
        visualTransformation =
            if (state.isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onAction(AuthAction.Submit) }),
    )
}

@Composable
@ReadOnlyComposable
private fun authErrorText(error: AuthError): String =
    stringResource(
        when (error) {
            AuthError.INVALID_USERNAME -> R.string.auth_error_username
            AuthError.INVALID_EMAIL -> R.string.auth_error_email
            AuthError.INVALID_PASSWORD -> R.string.auth_error_password
            AuthError.INVALID_CREDENTIALS -> R.string.auth_error_credentials
            AuthError.ACCOUNT_ALREADY_EXISTS -> R.string.auth_error_account_exists
            AuthError.STORAGE -> R.string.auth_error_storage
        },
    )

@Preview(showBackground = true)
@Composable
private fun RegistrationScreenPreview() {
    MaterialTheme {
        RegistrationScreen(state = AuthUiState(), onAction = {}, onLoginClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(state = AuthUiState(), onAction = {}, onRegisterClick = {})
    }
}
