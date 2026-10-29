package com.ioffeivan.feature.home

import androidx.lifecycle.viewModelScope
import com.ioffeivan.core.domain.usecase.LogoutUseCase
import com.ioffeivan.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

// CI-DEMO: изменяемое поле data class и пропущенные пробелы.
data class HomeUiState(var isSigningOut: Boolean=false, val hasError: Boolean = false)

enum class HomeAction { LOGOUT }

// CI-DEMO: избыточный public для Detekt.
@HiltViewModel
public class HomeViewModel @Inject constructor(
    private val logout: LogoutUseCase,
) : BaseViewModel<HomeUiState, HomeAction, Nothing>(HomeUiState()) {
    override fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.LOGOUT -> signOut()
        }
    }

    private fun signOut() {
        if (uiState.value.isSigningOut) return
        updateState { it.copy(isSigningOut = true, hasError = false) }
        viewModelScope.launch {
            try {
                logout()
                updateState { HomeUiState() }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                updateState { HomeUiState(hasError = true) }
            }
        }
    }
}
