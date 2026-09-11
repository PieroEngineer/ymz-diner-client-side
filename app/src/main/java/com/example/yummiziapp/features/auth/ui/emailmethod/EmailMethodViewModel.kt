package com.example.yummiziapp.features.auth.ui.emailmethod

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.core.data.network.model.SignUpData
import com.example.yummiziapp.core.utils.isEmailValid
import com.example.yummiziapp.core.utils.isPasswordValid
import com.example.yummiziapp.core.utils.isUsernameValid
import com.example.yummiziapp.features.auth.domain.model.AuthResult
import com.example.yummiziapp.features.auth.domain.repository.AuthRepository
import com.example.yummiziapp.features.user.domain.repository.UserRepository
import com.example.yummiziapp.features.auth.ui.model.UserInfoTab
import com.example.yummiziapp.features.auth.ui.utils.getAuthErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailMethodViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _effect = Channel<EmailMethodUiEvent>(Channel.BUFFERED)
    val navEffect = _effect.receiveAsFlow()

    private val _isSigningIn = MutableStateFlow(true)
    private val _formUiState = MutableStateFlow(FormUiModel())
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<EmailMethodUiState> = combine(
        _formUiState,
        _isSigningIn,
        _isLoading
    ){ formUiState, isSigningIn, isLoading ->
        if(isLoading){
            EmailMethodUiState.Loading
        }
        else{
            EmailMethodUiState.InForm(formUiState, isSigningIn)
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EmailMethodUiState.Loading
    )

    fun onTermsCheckBoxClicked(){
        if(!_formUiState.value.acceptTerms) _formUiState.update { it.copy(formIssueText = null) }
        _formUiState.update { it.copy(acceptTerms = !it.acceptTerms) }
    }

    fun onCreateAccountClicked() {
        viewModelScope.launch {

            Log.d("MainActivity" ,"🔎🪪  Starting sign up...")

            if(_isSigningIn.value){
                Log.d("MainActivity" ,"🔎🪪  Just toggle mode")
                _isSigningIn.update { false }
            }
            else{
                Log.d("MainActivity" ,"🔎🪪  No toggle, starting sign up...")

                _isLoading.update { true }

                val state = _formUiState.value
                if(!state.username.isUsernameValid()){  /// Maybe pass these errors to the Erro state of "EmailMethodUiState"
                    _formUiState.update { it.copy(formIssueText = "El nombre de usuario debe tener al menos 4 caracteres.") }
                    _isLoading.update { false }
                    return@launch
                }
                if(!state.email.isEmailValid()){
                    _formUiState.update { it.copy(formIssueText = "Por favor, ingrese un correo electrónico válido.") }
                    _isLoading.update { false }
                    return@launch
                }
                if(!state.password.isPasswordValid()){
                    _formUiState.update { it.copy(formIssueText = "La contraseña debe tener al menos 9 caracteres.") }
                    _isLoading.update { false }
                    return@launch
                }
                if(!state.acceptTerms){
                    _formUiState.update { it.copy(formIssueText = "Por favor, es necesario aceptar los términos y condiciones para continuar.") }
                    _isLoading.update { false }
                    return@launch
                }

                _formUiState.update { it.copy(formIssueText = null) }

                val signUpResult = authRepository.signUp(state.username, state.email, state.password)

                Log.d("MainActivity" ,"🔎🪪  Sign up result: $signUpResult")

                if(signUpResult is AuthResult.Success){
                    Log.d("MainActivity" ,"🔎🪪  Sign up was successful")
                    val signInResult = authRepository.signIn(state.email, state.password, state.username)
                    if(signInResult is AuthResult.Success){
                        Log.d("MainActivity" ,"🔎🪪  Sign in just after sign up was successful")
                        userRepository.createNewUser(state.username)
                        _effect.send(EmailMethodUiEvent.NavigateToIntro)
                    }
                    else{
                        Log.d("MainActivity" ,"🔎🪪  Sign in just after sign up was not successful")
                        signInResult as AuthResult.Error
                        _formUiState.update { it.copy(formIssueText = getAuthErrorMessage(signInResult.exception)) }
                        _isLoading.update { false }
                    }
                }
                else{
                    Log.d("MainActivity" ,"🔎🪪  Sign up was not successful")
                    signUpResult as AuthResult.Error
                    _formUiState.update { it.copy(formIssueText = getAuthErrorMessage(signUpResult.exception)) }
                    _isLoading.update { false }
                }
            }
        }
    }

    fun onAccessAccountClicked() {
        viewModelScope.launch {
            _isLoading.update { true }
            Log.d("MainActivity" ,"🔎🪪  Starting sign in...")

            val state = _formUiState.value
            if(!state.email.isEmailValid()){
                _formUiState.update { it.copy(formIssueText = "Por favor, ingrese un correo electrónico válido.") }
                _isLoading.update { false }
                return@launch
            }

            _formUiState.update { it.copy(formIssueText = null) }

            val result = authRepository.signIn(state.email, state.password)
            if(result is AuthResult.Success){
                Log.d("MainActivity" ,"🔎🪪  Sign in was successful")
                _effect.send(EmailMethodUiEvent.NavigateToHome)
            }
            else{
                Log.d("MainActivity" ,"🔎🪪  Sign in was not successful")
                result as AuthResult.Error
                _formUiState.update { it.copy(formIssueText = getAuthErrorMessage(result.exception)) }
                _isLoading.update { false }
            }
        }
    }

    fun onFormTextModified(newText: String, userInfoTab: UserInfoTab) {
        _formUiState.update{
            when(userInfoTab){
                UserInfoTab.USERNAME -> it.copy(username = newText)
                UserInfoTab.EMAIL -> it.copy(email = newText)
                UserInfoTab.PASSWORD -> it.copy(password = newText)
            }
        }
    }
}