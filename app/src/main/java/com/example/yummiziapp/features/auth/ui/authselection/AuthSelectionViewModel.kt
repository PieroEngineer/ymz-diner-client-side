package com.example.yummiziapp.features.auth.ui.authselection

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.features.auth.domain.model.AuthResult
import com.example.yummiziapp.features.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthSelectionViewModel  @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel(){

    // StateFlow retains value for configuration changes
    private val _uiState = MutableStateFlow<AuthSelectionUiState>(AuthSelectionUiState.Loading)
    val uiState: StateFlow<AuthSelectionUiState> = _uiState.asStateFlow()

    private val _effect = Channel<AuthSelectionUiEvents>(Channel.BUFFERED)
    val navEffect = _effect.receiveAsFlow()

    fun onEmailLoginClicked(){
        viewModelScope.launch {
            _effect.send(AuthSelectionUiEvents.NavigateToEmailLogin)
        }
    }

    init {
        // Automatically check if persistent session exists on instantiation
        checkExistingSession()
    }

    fun checkExistingSession() {
        viewModelScope.launch {
            _uiState.update { AuthSelectionUiState.Loading }

            when (val result = authRepository.checkSession()) { /// Change it by ".onSuccess" way?
                is AuthResult.Success -> {
                    val isSignedIn = result.data

                    Log.i("MyActivity", "🔎🪪  Existing session found: $isSignedIn")

                    if (isSignedIn) {
                        _effect.send(AuthSelectionUiEvents.NavigateToHome)
                    }
                    else {
                        _uiState.update { AuthSelectionUiState.UnauthenticatedSession }
                    }
                }
                is AuthResult.Error -> {
                    Log.d("MyActivity", "🚩🪪  Error when trying to check session: ${result.message}")

                    _uiState.update { AuthSelectionUiState.UnauthenticatedSession }

                    Log.e("MyActivity", "🚩👉  Error when trying to check session: ${result.message}")

                // TODO: Trigger a snackbar with the "result.message" of the session
                }
            }

        }
    }
}