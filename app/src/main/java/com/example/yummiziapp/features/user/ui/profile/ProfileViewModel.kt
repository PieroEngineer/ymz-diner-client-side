package com.example.yummiziapp.features.user.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.features.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
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
            // TODO: Update the state to loading immediately

            authRepository.signOut()

            _effect.send(ProfileUiEvent.NavigateToLogin)
        }
    }
}