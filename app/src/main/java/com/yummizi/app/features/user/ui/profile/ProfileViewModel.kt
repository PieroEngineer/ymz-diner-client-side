package com.yummizi.app.features.user.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yummizi.app.features.auth.domain.repository.AuthRepository
import com.yummizi.app.features.user.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
): ViewModel(){

    private val _effect = Channel<ProfileUiEvent>(Channel.BUFFERED)
    val navEffect = _effect.receiveAsFlow()

    fun onHomeClicked(){
        viewModelScope.launch {
            _effect.send(ProfileUiEvent.NavigateToHome)
        }
    }
    fun onContactClicked(){
        viewModelScope.launch {
            _effect.send(ProfileUiEvent.NavigateToContact)
        }
    }

    fun onLogoutClicked(){
        viewModelScope.launch {
            // TODO (#502): Update the state to loading immediately

            authRepository.signOut()

            _effect.send(ProfileUiEvent.NavigateToLogin)
        }
    }

    val profileUiState: StateFlow<ProfileUiState> = userRepository.observeCurrentUserInfo() // TODO: Replace the UI model by a UI state that includes that UI model
        .map{ user ->

            if (user != null) {
                ProfileUiState.Success(
                    ProfileUiModel(
                        username = user.name
                    )
                ) as ProfileUiState
            }
            else{
                ProfileUiState.Error("Usuario no encontrado")
            }

        }
        .catch { error -> emit(ProfileUiState.Error(error.message ?: "Error al cargar el usuario")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileUiState.Loading
        )

}