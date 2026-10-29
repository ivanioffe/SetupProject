package com.ioffeivan.setupproject

import androidx.lifecycle.viewModelScope
import com.ioffeivan.core.domain.model.User
import com.ioffeivan.core.domain.usecase.ObserveCurrentUserUseCase
import com.ioffeivan.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SessionState {
    data object Loading : SessionState

    data object SignedOut : SessionState

    data class SignedIn(val user: User) : SessionState

    data object StorageError : SessionState
}

enum class SessionAction { RETRY }

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val observeCurrentUser: ObserveCurrentUserUseCase,
) : BaseViewModel<SessionState, SessionAction, Nothing>(SessionState.Loading) {
    private var observation: Job? = null

    init {
        observeSession()
    }

    override fun onAction(action: SessionAction) {
        when (action) {
            SessionAction.RETRY -> observeSession()
        }
    }

    private fun observeSession() {
        observation?.cancel()
        updateState { SessionState.Loading }
        observation =
            viewModelScope.launch {
                try {
                    observeCurrentUser().collect { user ->
                        updateState { user?.let(SessionState::SignedIn) ?: SessionState.SignedOut }
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    updateState { SessionState.StorageError }
                }
            }
    }
}
