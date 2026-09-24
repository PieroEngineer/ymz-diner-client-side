package com.yummizi.app.features.home.ui.feed

import com.yummizi.app.features.home.ui.model.SectionUiModel

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val sections: List<SectionUiModel>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}