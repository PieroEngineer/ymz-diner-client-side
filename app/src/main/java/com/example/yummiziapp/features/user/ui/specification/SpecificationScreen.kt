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
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.core.ui.components.NavigationBar
import com.example.yummiziapp.core.ui.components.SearchingArea
import com.example.yummiziapp.features.user.ui.specification.components.CompanySection
import com.example.yummiziapp.features.user.ui.specification.components.Diner
import com.example.yummiziapp.features.user.ui.specification.components.FocusedRestaurantFrame
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

        Column( // Complete option frame
            modifier = modifier
                .padding(horizontal = spacing.mediumPadding)
        ) {

            // Testing data
            val diner1 = Diner(1, "Grabiel")
            val diner2 = Diner(2, "Sonia")
            val diner3 = Diner(3, "Albert")
            val diner4 = Diner(4, "Marco")
            val diner5 = Diner(5, "Yeni")
            val diner6 = Diner(6, "Kriss")

            val currentFriends = listOf(diner1, diner2, diner3, diner4, diner5, diner6)

            // "Your selection" section
            Log.d("MyActivity", "🔎📱  Just before Specification section")
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

            // "Company" section
            CompanySection(
                Modifier
                    .padding(horizontal = spacing.mediumPadding)
                    .fillMaxWidth()
                    .zIndex(2f),
                currentFriends
            )

            when (val state = uiState) {
                is SpecificationUiState.Loading -> {
                    // Loading UI
                }
                is SpecificationUiState.ListingRestaurants -> {
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

                        onQueuedRestaurantClicked = {
                            viewModel.onQueuedRestaurantClicked(it)
                        },
                        onAcceptedRestaurantClicked = {
                            viewModel.onAcceptedRestaurantClicked(it)
                        },
                        onDeclinedRestaurantClicked = {
                            viewModel.onDeclinedRestaurantClicked(it)
                        },

                    )
                }
                is SpecificationUiState.FocusingRestaurant -> {
                    FocusedRestaurantFrame(
                        Modifier
                            .padding(horizontal = spacing.mediumPadding)
                            .fillMaxWidth()
                            .zIndex(1f)
                            .weight(1f),

                        selectedRestaurant = state.restaurant,

                        onQueueJoiningClicked ={ arrivalTimeInMinutes, restaurantId -> // TODO: Include some kind of confirmation when the restaurant was queued even if the orange frame already does it.
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

}