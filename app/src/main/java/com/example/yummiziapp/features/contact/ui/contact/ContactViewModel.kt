package com.example.yummiziapp.features.contact.ui.contact

import androidx.lifecycle.ViewModel
import com.example.yummiziapp.features.contact.domain.repository.ContactRepository
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.core.data.local.model.FeedbackLevelToken
import com.example.yummiziapp.features.contact.ui.model.FeedbackUiModel
import com.example.yummiziapp.features.contact.ui.model.RestaurantInfoTab
import com.example.yummiziapp.features.contact.ui.model.RestaurantInfoUiModel
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
class ContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository
): ViewModel() {

    private val _effect = Channel<ContactUiEvent>(Channel.BUFFERED)
    val navEffect = _effect.receiveAsFlow()

    private val _feedbackUiModel = MutableStateFlow(FeedbackUiModel())
    val feedbackUiModel: StateFlow<FeedbackUiModel> = _feedbackUiModel.asStateFlow()

    private val _restaurantContactUiModel = MutableStateFlow(RestaurantInfoUiModel())
    val restaurantContactUiModel: StateFlow<RestaurantInfoUiModel> = _restaurantContactUiModel.asStateFlow()

    fun onFeedbackDescriptionChanged(newText: String) {
        _feedbackUiModel.update { currentState ->
            currentState.copy(description = newText)
        }
    }

    fun onCopyYummiziLink(){    // TODO: Track this action remotely
        viewModelScope.launch {
            _effect.send(ContactUiEvent.CopyToClipboard(
                copiedText = "https://yummizi.netlify.app/",
                accompanyingText = "📢  Link para compartir copiado"
            ))
        }
    }

    fun onConformityDegreeChanged(newDegree: FeedbackLevelToken) {
        _feedbackUiModel.update { currentState ->
            currentState.copy(conformityDegree = newDegree)
        }
    }

    fun onRestaurantContactTextFieldChanged(restaurantInfo: RestaurantInfoTab, newText: String){
        _restaurantContactUiModel.update { currentState ->
            when (restaurantInfo) {
                RestaurantInfoTab.NAME -> currentState.copy(name = newText)
                RestaurantInfoTab.CONTACT -> currentState.copy(contact = newText)
                RestaurantInfoTab.COMMENT -> currentState.copy(comment = newText)
            }
        }
    }

    fun onFeedbackReady(){  /// I didn't used a UI state in this ViewModel because the screen is local and the remote sending is fast, but conisder create it anyway
        val currentFeedbackUiState = _feedbackUiModel.value
        viewModelScope.launch {
            contactRepository.sendFeedback(currentFeedbackUiState.conformityDegree, currentFeedbackUiState.description)
        }

        _feedbackUiModel.update{ FeedbackUiModel() }    // TODO: Change this by the connection feedback
    }

    fun onRestaurantContactReady(){  // TODO: Include a logic that only allow
        val currentRestaurantContactUiState = _restaurantContactUiModel.value
        viewModelScope.launch {
            contactRepository.sendRestaurantInfo(
                currentRestaurantContactUiState.name,
                currentRestaurantContactUiState.contact,
                currentRestaurantContactUiState.comment
            )

            _restaurantContactUiModel.update { RestaurantInfoUiModel() }
        }
    }

    fun onTryToSeeVideo() {
        viewModelScope.launch {
            _effect.send(ContactUiEvent.IntentNavigateToLink("https://www.youtube.com/watch?v=7b_l_gql7mM"))
        }
    }

    fun onHomeClicked() {
        viewModelScope.launch {
            _effect.send(ContactUiEvent.NavigateToHome)
        }
    }

    fun onYouClicked() {
        viewModelScope.launch {
            _effect.send(ContactUiEvent.NavigateToPreferences)
        }
    }

}