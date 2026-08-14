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
import com.example.yummiziapp.features.auth.ui.model.UserInfoTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailMethodViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

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

    fun onCreateAccountClicked() {
        viewModelScope.launch {
            _isLoading.update { true }

            if(_isSigningIn.value){
                _isSigningIn.update { false }
            }
            else{
                val state = _formUiState.value
                if(!state.username.isUsernameValid()){  /// Maybe pass these errors to the Erro state of "EmailMethodUiState"
                    _formUiState.update { it.copy(formIssueText = "El nombre de usuario debe tener al menos 4 caracteres.") }
                    return@launch
                }
                if(!state.email.isEmailValid()){
                    _formUiState.update { it.copy(formIssueText = "Por favor, ingrese un correo electrónico válido.") }
                    return@launch
                }
                if(!state.password.isPasswordValid()){
                    _formUiState.update { it.copy(formIssueText = "La contraseña debe tener al menos 6 caracteres.") }
                    return@launch
                }
                if(!state.acceptTerms){
                    _formUiState.update { it.copy(formIssueText = "Por favor, es necesario aceptar los términos y condiciones para continuar.") }
                    return@launch
                }

                _formUiState.update { it.copy(formIssueText = null) }

                val signUpResult = authRepository.signUp(state.username, state.email, state.password)

                if(signUpResult is AuthResult.Success){
                    val signInResult = authRepository.signIn(state.email, state.password)
                    if(signInResult is AuthResult.Success){
                        ///✏️Navigate to Intro
                    }
                    else{
                        signInResult as AuthResult.Error
                        _formUiState.update { it.copy(formIssueText = "La autenticación falló (${signInResult.message})") }
                    }
                }
                else{
                    signUpResult as AuthResult.Error
                    _formUiState.update { it.copy(formIssueText = "La creación de cuenta falló (${signUpResult.message})") }
                }

            }

        }

    }

    fun onTermsCheckBoxClicked(){
        if(!_formUiState.value.acceptTerms) _formUiState.update { it.copy(formIssueText = null) }
        _formUiState.update { it.copy(acceptTerms = !it.acceptTerms) }
    }

    fun onAccessAccountClicked() {
        viewModelScope.launch {
            _isLoading.update { true }

            val state = _formUiState.value
            if(!state.email.isEmailValid()){
                _formUiState.update { it.copy(formIssueText = "Por favor, ingrese un correo electrónico válido.") }
                return@launch
            }

            _formUiState.update { it.copy(formIssueText = null) }
            Log.d("MyActivity", "Access to the account is valid")


            val result = authRepository.signIn(state.email, state.password)
            if(result is AuthResult.Success){
                ///✏️Navigate to home
            }
            else{
                result as AuthResult.Error
                _formUiState.update { it.copy(formIssueText = "La autenticación falló (${result.message})") }
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