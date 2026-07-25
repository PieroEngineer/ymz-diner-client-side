package com.example.yummiziapp.features.user.ui.specification

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.core.ui.components.NavigationBar
import com.example.yummiziapp.core.ui.components.SearchingArea
import com.example.yummiziapp.features.user.ui.specification.components.CompanySection
import com.example.yummiziapp.features.user.ui.specification.components.FocusedRestaurantFrame
import com.example.yummiziapp.features.user.ui.specification.components.AcceptanceConfirmationModal
import com.example.yummiziapp.features.user.ui.specification.components.SelectionSection
import com.example.yummiziapp.features.user.ui.specification.components.SpecificationSection
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

    val uiRestaurantState by viewModel.uiRestaurantState.collectAsState()
    val uiUserState by viewModel.uiUserState.collectAsState()
    var isConfirmationPopupVisible by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ){

        // Screen content
        Column(
            modifier = modifier
                .padding(vertical = spacing.mediumPadding)
                .fillMaxSize()
                .background(YummiziTheme.colors.yummiziWhite)
        ){
            // Searching part
            SearchingArea(
                Modifier
                    .padding(spacing.mediumPadding)
            )

            Spacer(Modifier.height(spacing.regularSeparation))

            Column( // Complete option frame
                modifier = modifier
                    .padding(horizontal = spacing.mediumPadding)
            ) {

                // "Your selection" section
                SpecificationSection(
                    Modifier
                        .padding(horizontal = spacing.mediumPadding)
                        .fillMaxWidth()
                        .zIndex(3f),

                    mapOf(  //T This is just for testing. The real is a map too, but come from user's backend
                        "is special date" to true,
                        "have baby" to false,
                        "need support" to true
                    )
                )

                when (val state = uiUserState) {
                    is UserSpecificationUiState.Loading -> {
                        // Loading UI
                    }
                    is UserSpecificationUiState.Success -> {
                        CompanySection(     // "Company" section
                            Modifier
                                .padding(horizontal = spacing.mediumPadding)
                                .fillMaxWidth()
                                .zIndex(2f),
                            state.details,
                            onCompanySizeUpdated = { isIncreasing -> viewModel.onCompanySizeUpdated(isIncreasing) }
                        )
                    }
                    is UserSpecificationUiState.Error -> {
                        Log.d("MyActivity", "🚩👉  Error when trying to see the specifications: ${state.errorMessage}")
                    }
                }

                when (val state = uiRestaurantState) {
                    is RestaurantSpecificationUiState.Loading -> {
                        Log.d("MyActivity", "🔎🔃  The specifications are loading")
                        // Loading UI
                    }
                    is RestaurantSpecificationUiState.ListingRestaurants -> {

//X                        Log.d("MyActivity", "🔎🥗  Restaurants to be shown: ${state.restaurants}")

                        SelectionSection(
                            Modifier
                                .padding(horizontal = spacing.mediumPadding)
                                .fillMaxWidth()
                                .zIndex(1f)
                                .weight(1f),

                            listedRestaurants = state.restaurants,

                            onRemoveRestaurant = { id ->
                                viewModel.leaveQueue(id)
                            },

                            onQueuedRestaurantClicked = { id ->
                                viewModel.onQueuedRestaurantClicked(id)
                            },
                            onAcceptedRestaurantClicked = { id ->
                                viewModel.onAcceptedRestaurantClicked(id)
                            },
                            onDeclinedRestaurantClicked = { id ->
                                viewModel.onDeclinedRestaurantClicked(id)
                            },

                        )
                    }
                    is RestaurantSpecificationUiState.FocusingRestaurant -> {
                        FocusedRestaurantFrame(
                            Modifier
                                .padding(horizontal = spacing.mediumPadding)
                                .fillMaxWidth()
                                .zIndex(1f)
                                .weight(1f),

                            selectedRestaurant = state.restaurant,

                            onQueueJoiningClicked ={ arrivalTimeInMinutes, restaurantId -> // TODO: Include some kind of confirmation when the restaurant was queued even if the orange frame already does it.
                                viewModel.onQueueJoiningClicked(arrivalTimeInMinutes, restaurantId)
                            },

                            onTryToConfirmAcceptanceClicked = {
                                Log.e("MyActivity", "🔎🍽️  Trying to confirm acceptance")

                                isConfirmationPopupVisible = true
                            }
                        )
                    }
                    is RestaurantSpecificationUiState.Error -> {
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

        // Insert the Modal at the absolute bottom of the root Box structure
        AcceptanceConfirmationModal(
            isOpen = isConfirmationPopupVisible,
            onDismissRequest = { isConfirmationPopupVisible = false },
            onConfirmationTriggered = {
                // Close the visual window shield immediately
                isConfirmationPopupVisible = false

                // Instruct the ViewModel brain to fire off the background business tasks
                viewModel.onAcceptanceConfirmed()
            }
        )

    }

}