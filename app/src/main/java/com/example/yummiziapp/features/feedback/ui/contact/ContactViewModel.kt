package com.example.yummiziapp.features.feedback.ui.contact

import androidx.lifecycle.ViewModel
import com.example.yummiziapp.features.feedback.domain.repository.FeedbackRepository
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.features.user.ui.specification.SpecificationUiEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val feedbackRepository: FeedbackRepository
): ViewModel() {

    private val _effect = Channel<ContactUiEvent>(Channel.BUFFERED)
    val navEffect = _effect.receiveAsFlow()

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