package com.example.yummiziapp.features.auth.ui.emailmethod

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.yummiziapp.core.utils.isEmailValid
import com.example.yummiziapp.core.utils.isNotEmpty
import com.example.yummiziapp.core.utils.isPasswordValid
import com.example.yummiziapp.core.utils.isUsernameValid
import com.example.yummiziapp.features.auth.ui.model.UserInfoTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EmailMethodViewModel @Inject constructor(

) : ViewModel() {

    private val _isSigningIn = MutableStateFlow(true)
    val isSigningIn: StateFlow<Boolean> = _isSigningIn.asStateFlow()

    private val _formUiState = MutableStateFlow(FormUiModel())
    val formUiState: StateFlow<FormUiModel> = _formUiState.asStateFlow()

    fun onCreateAccountClicked() {
        if(_isSigningIn.value){
            _isSigningIn.update { false }
        }
        else{
            val state = _formUiState.value
            if(!state.username.isUsernameValid()){
                _formUiState.update { it.copy(formIssueText = "El nombre de usuario debe tener al menos 4 caracteres.") }
                return
            }
            if(!state.email.isEmailValid()){
                _formUiState.update { it.copy(formIssueText = "Por favor, ingrese un correo electrónico válido.") }
                return
            }
            if(!state.password.isPasswordValid()){
                _formUiState.update { it.copy(formIssueText = "La contraseña debe tener al menos 6 caracteres.") }
                return
            }
            if(!state.acceptTerms){
                _formUiState.update { it.copy(formIssueText = "Por favor, es necesario aceptar los términos y condiciones para continuar.") }
                return
            }

            _formUiState.update { it.copy(formIssueText = null) }
            Log.d("MyActivity", "Create Account: Form is valid")
            ///Start loading state
            ///Upload info to backend
            ///Save user in the Room
            ///If it's the first time of the user (account didn't exist previously), navigate to the Intro screen

        }
    }

    fun onTermsCheckBoxClicked(){
        if(!_formUiState.value.acceptTerms) _formUiState.update { it.copy(formIssueText = null) }
        _formUiState.update { it.copy(acceptTerms = !it.acceptTerms) }
    }

    fun onAccessAccountClicked() {
        val state = _formUiState.value
        if(!state.email.isEmailValid()){
            _formUiState.update { it.copy(formIssueText = "Por favor, ingrese un correo electrónico válido.") }
            return
        }

//X The app shouldn't reveal the correct pattern of a password
//        if(!state.password.isPasswordValid()){
//            _formUiState.update { it.copy(formIssueText = "La contraseña debe tener al menos 6 caracteres.") }
//            return
//        }

        _formUiState.update { it.copy(formIssueText = null) }
        Log.d("MyActivity", "Access to the account is valid")
        ///Verify sign in the backend
        ///Navigate to home screen

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