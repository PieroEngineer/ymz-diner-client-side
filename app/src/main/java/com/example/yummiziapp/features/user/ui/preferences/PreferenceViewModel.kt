package com.example.yummiziapp.features.user.ui.preferences

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yummiziapp.core.data.local.entities.Preference
import com.example.yummiziapp.features.user.domain.repository.UserRepository
import com.example.yummiziapp.features.user.ui.model.PreferenceUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class PreferenceViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    var titleInput by mutableStateOf("")
    var descInput by mutableStateOf("")
    var colorInput by mutableStateOf("#FFFFFF")

    val preferences: StateFlow<List<PreferenceUiModel>> = userRepository
        .observePreferences()
        .map { entities ->
            entities.map { entity ->
                PreferenceUiModel(
                    id = entity.id,
                    title = entity.title,
                    descriptionPart = entity.description,  //TODO: limit its number of characters because it's within the item
                    color = Color(colorInput.toColorInt()),
                    isSyncing = !entity.noSyncError
                )
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun onSaveClicked() {
        if (!titleInput.isBlank()){

            val newPreference = Preference(
                id = Uuid.random(),
                userId = Uuid.parse("00000000-0000-4000-a000-000000000000"),    //TODO: check if it's a bad practice to let this empty here and then fill it in the repository
                title = titleInput,
                description = descInput,
                color = colorInput,
                noSyncError = true
            )
            val lastTitle = titleInput
            val lastDesc = descInput
            val lastColor = colorInput

            viewModelScope.launch {

//                try{
                val result = userRepository.savePreference(newPreference)
                // Clear inputs after clicking save
                titleInput = ""
                descInput = ""

                result.onFailure{ error ->
                    Log.e("MyActivity", "❌ Fail to save preference", error)
                    titleInput = lastTitle
                    descInput = lastDesc
                    colorInput = lastColor
                }

//                }
//                catch (e: Exception){
//
//
//                    Log.e("MyActivity", "❌ Fail to save preference", e)
//                    titleInput = lastTitle
//                    descInput = lastDesc
//                    colorInput = lastColor
//                }


            }

        }
    }

    fun onDeletePreference(prefId: Uuid) {
        viewModelScope.launch {
            try {
                // Trigger the repository (Optimistic Delete)
                userRepository.deletePreference(prefId)

                // Show a Snackbar with an Undo action
//            val result = snackbarHostState.showSnackbar(
//                message = "Preference deleted",
//                actionLabel = "Undo",
//                duration = SnackbarDuration.Short
//            )

                // If user clicks "Undo", we simply toggle the flag back
//            if (result == SnackbarResult.ActionPerformed) {
//                repository.undoDeletePreference(prefId)
//            }
            }
            catch (e: Exception){
                Log.e("MyActivity", "❌ Fail to delete preference", e)
            }
        }
    }
}