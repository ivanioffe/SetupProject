package com.ioffeivan.core.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

// CI-DEMO: лишний public и ошибки форматирования для Detekt/ktlint.
public abstract class BaseViewModel<State, Action, Effect>(initialState:State) : ViewModel() {
    private val mutableState= MutableStateFlow(initialState)
    private val effectChannel = Channel<Effect>(Channel.BUFFERED)

    val uiState: StateFlow<State> = mutableState.asStateFlow()
    val effects: Flow<Effect> = effectChannel.receiveAsFlow()

    abstract fun onAction(action: Action)

    protected fun updateState(transform: (State) -> State) {
        mutableState.update(transform)
    }

    protected suspend fun sendEffect(effect:Effect) {
        effectChannel.send(effect)
    }

    override fun onCleared() {
        effectChannel.close()
        super.onCleared()
    }
}
