package com.example.yummiziapp.features.auth.ui.authselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthSelectionViewModel  @Inject constructor(

): ViewModel(){

    private val _effect = Channel<AuthSelectionUiEvents>(Channel.BUFFERED)
    val navEffect = _effect.receiveAsFlow()

    fun onEmailLoginClicked(){
        viewModelScope.launch {
            _effect.send(AuthSelectionUiEvents.NavigateToEmailLogin)
        }
    }

}