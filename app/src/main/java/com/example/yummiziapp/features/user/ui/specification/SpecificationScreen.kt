package com.example.yummiziapp.features.user.ui.specification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    val currentListedRestaurants by viewModel.queuedRestaurants.collectAsStateWithLifecycle()

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

        // Option frame
        OptionFrame(
            Modifier.weight(1f),

            onRestaurantSelected = { id ->
                viewModel.onRestaurantClicked(id)
            },
            onRemoveRestaurant = { id ->
                viewModel.leaveQueue(id)
            },
            listedRestaurants = currentListedRestaurants
        )

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