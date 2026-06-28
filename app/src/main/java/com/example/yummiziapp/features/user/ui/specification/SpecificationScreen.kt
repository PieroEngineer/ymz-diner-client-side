package com.example.yummiziapp.features.user.ui.specification

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.core.ui.components.NavigationBar
import com.example.yummiziapp.core.ui.components.SearchingArea
import com.example.yummiziapp.features.user.ui.specification.components.OptionFrame
import kotlin.uuid.Uuid
//import androidx.compose.ui.tooling.preview.Preview

//@Preview(showBackground = false)
@Composable
fun SpecificationScreen(
    modifier: Modifier = Modifier,
    viewModel: SpecificationViewModel = hiltViewModel(),
    onRestaurantSelectedNav: (Uuid) -> Unit,
    onHomeClickedNav: () -> Unit,
    onYouClickedNav: () -> Unit,
) {
    val spacing = YummiziTheme.spacing

    // Listen for the ViewModel's commands
    LaunchedEffect(Unit) {
        viewModel.navEffect.collect { effect ->
            when (effect) {
                is SpecificationUiEvents.NavigateToRestaurantMenu -> { onRestaurantSelectedNav(effect.restaurantId) }
                is SpecificationUiEvents.NavigateToHome -> { onHomeClickedNav() }
                is SpecificationUiEvents.NavigateToPreferences -> { onYouClickedNav() }
            }
        }
    }

//X    val currentListedRestaurants by viewModel.queuedRestaurants.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsState()

    // Screen content
    Column(
        modifier = modifier
            .padding(vertical = spacing.mediumPadding)
            .fillMaxSize()
            .background(YummiziTheme.colors.yummziziWhite)
    ){
        // Searching part
        SearchingArea(
            Modifier
                .padding(spacing.mediumPadding)
        )

        Spacer(Modifier.height(spacing.regularSeparation))


        when (val state = uiState) {
            is SpecificationUiState.Loading -> {
                // Loading UI
            }
            is SpecificationUiState.ListingRestaurants -> {
                OptionFrame(    // TODO: Move the customer's specifics and company out of this and convert these "OptionFrame" into 2 different components
                    Modifier.weight(1f),

                    onRestaurantSelected = { id ->
                        viewModel.onRestaurantClicked(id)
                    },
                    onRemoveRestaurant = { id ->
                        viewModel.leaveQueue(id)
                    },
                    listedRestaurants = state.restaurants,
                )
            }
            is SpecificationUiState.FocusingRestaurant -> {
                Log.d("MyActivity", "🔎📱  Arrive to the screen with the focused restaurant")
                OptionFrame(
                    Modifier.weight(1f),

                    focusedRestaurant = state.restaurant,

                    onQueueJoiningClicked = { arrivalTimeInMinutes, restaurantId -> // TODO: Include some kind of confirmation when the restaurant was queued even if the orange frame already does it.
                        viewModel.onQueueJoiningClicked(arrivalTimeInMinutes, restaurantId)
                    }
                )
            }
            is SpecificationUiState.Error -> {
                Log.d("MyActivity", "🚩👉  Error when trying to see the specifications: ${state.errorMessage}")
            }

        }


        Spacer(Modifier.height(spacing.regularSeparation))

        // Navigation part
        NavigationBar(
            Modifier
                .fillMaxWidth()
                .height(spacing.barHeight),

            homeHighlighted = true,
            onHomeClicked = { viewModel.onHomeClicked() },
            onYouClicked = { viewModel.onYouClicked() },

            shadowOn = false
        )

    }

}