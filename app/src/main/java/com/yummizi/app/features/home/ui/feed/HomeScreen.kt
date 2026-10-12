package com.yummizi.app.features.home.ui.feed

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yummizi.app.core.data.local.entities.Restaurant
import com.yummizi.app.core.designsystem.theme.YummiziTheme
import com.yummizi.app.features.home.ui.feed.components.SelectionFrame
import kotlin.uuid.Uuid
import androidx.hilt.navigation.compose.hiltViewModel
import com.yummizi.app.core.ui.components.NavigationBar
import com.yummizi.app.core.ui.components.SearchingArea
import com.yummizi.app.core.ui.components.DiffRoundedCornersRect
import com.yummizi.app.core.ui.components.YummiziSnackbar
import com.yummizi.app.features.home.ui.feed.components.DragOverlay
import com.yummizi.app.features.home.ui.feed.components.FeedSection
import com.yummizi.app.features.home.ui.feed.components.MiniTutorialModal
import com.yummizi.app.features.home.ui.feed.components.RestaurantBlock
import com.yummizi.app.features.home.ui.feed.components.RestaurantScrollable
import kotlin.collections.forEach


//@Preview(showBackground = true)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onRestaurantSelectionNavigation: (Uuid?) -> Unit,
    onRestaurantSelected: (Uuid) -> Unit,
    onReadyRestaurantSelected: (Uuid) -> Unit,
    onPaidRestaurantSelected: (Uuid) -> Unit,
    onPreferenceNavigation: () -> Unit,
    onContactNavigation: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val snackbarHostState = remember { SnackbarHostState() }

    // Listen for the ViewModel's commands
    LaunchedEffect(Unit) {
        viewModel.navEffect.collect { effect ->
            when (effect) {
                is HomeUiEvents.NavigateToRestaurantSelectionFrame -> { onRestaurantSelectionNavigation(effect.restaurantId) }
                is HomeUiEvents.NavigateToRestaurantMenuPreview -> { onRestaurantSelected(effect.restaurantId) }
                is HomeUiEvents.NavigateToReadyRestaurantMenu -> { onReadyRestaurantSelected(effect.restaurantId) }
                is HomeUiEvents.NavigateToPaidRestaurant -> { onPaidRestaurantSelected(effect.restaurantId) }
                is HomeUiEvents.ShowConnectionError -> { /* ... */ }
                is HomeUiEvents.NavigateToPreferences -> { onPreferenceNavigation() }
                is HomeUiEvents.NavigateToContact -> { onContactNavigation() }
                is HomeUiEvents.SnackBar -> {
                    snackbarHostState.showSnackbar(message = effect.message)
                }
            }
        }
    }

    val spacing = YummiziTheme.spacing

    // State variables
    var draggingRestaurant by remember { mutableStateOf<Restaurant?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var grabOffset by remember { mutableStateOf(Offset.Zero) }
    var dropZoneBounds by remember { mutableStateOf<Rect?>(null) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activeRestaurantsUiState by viewModel.activeRestaurantsUiState.collectAsStateWithLifecycle()
    val miniTutorialUiState by viewModel.miniTutorialUiState.collectAsStateWithLifecycle()

    // Hoisted State: Controlled completely at the Screen Orchestration Layer
    var currentControlledSectionId by rememberSaveable { mutableStateOf<Int?>(null) }

    // Animate scale: 0.9f when pressed, 1f when released
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        label = "scaleAnimation"
    )

    val density = LocalDensity.current

    // Screen content
    Box(
        modifier = modifier.fillMaxSize()
    ){  // Main container
        Column( // Main column
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            // Searching part
            SearchingArea(
                Modifier
                    .padding(spacing.mediumPadding)
            )

            Spacer(Modifier.height(spacing.mediumSeparation))

            // Frame part
            SelectionFrame(
                modifier = Modifier
                    .height(spacing.frameHeight)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .padding(horizontal = spacing.mediumPadding)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null, // Removes the ripple
                        onClick = { viewModel.onSelectionFrameClicked() }
                    ),

                activeRestaurantsUiState = activeRestaurantsUiState,
                onBoundsChanged = { bounds ->
                    dropZoneBounds = bounds
                }
            )

            Spacer(Modifier.height(spacing.regularSeparation))

            // Visual separator
            DiffRoundedCornersRect(
                Modifier
                    .height(spacing.mediumSeparation)
                    .fillMaxWidth(),

                topStart = spacing.tinyCornerRadius,
                topEnd = spacing.tinyCornerRadius,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            )

            // Section part
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Text(
                        text = "Loading data...",
                        textAlign = TextAlign.Start,
                        color = YummiziTheme.colors.yummiziDarkBlue,
                        style = YummiziTheme.typography.teacherFontVar3
                    )
                }
                is HomeUiState.Success -> {

                    // Slot Composition Approach
//                    SectionArea(    // Section area takes it parameters and pass the others to its child  // TODO (#830)
//                        Modifier
//                            .weight(1f)
//                            .background(YummiziTheme.colors.yummiziWhite),
//                    ) {
//                        // Lazy items and keys perfectly right here
//                        items(
//                            items = state.sections,
//                            key = { section -> section.id }
//                        ) { section ->


                Column( // X // TODO (#830)
                    modifier = Modifier.weight(1f),
//                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(spacing.mediumSeparation),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    state.sections.forEach { section ->
                        key(section.id){

                            ///
                            FeedSection(
                                // Feed section takes it parameters and pass the others to its child
                                Modifier
                                    .fillMaxWidth(),

                                section.title,
                            ) {
                                RestaurantScrollable(
                                    // Restaurant scrollable takes it parameters and pass the others to its child
                                    Modifier,

                                    section.id,
                                    currentControlledSectionId == section.id,

                                    { controlledSectionId ->
                                        currentControlledSectionId = controlledSectionId
                                    },
                                ) {
                                    items(
                                        items = section.restaurants,
                                        key = { restaurant -> restaurant.id }
                                    ) { restaurant ->

                                        RestaurantBlock(
                                            Modifier
                                                .width((section.restaurantBlockHeight * 2).dp)
                                                .height(section.restaurantBlockHeight.dp)
                                                .clickable {
                                                    viewModel.onRestaurantClicked(
                                                        restaurant.id
                                                    )
                                                },

                                            restaurant,
                                            currentControlledSectionId == section.id,

                                            { restaurant, startPos ->
                                                //If there is a restaurant ready, don't allow drag
                                                if (activeRestaurantsUiState is ActiveRestaurantsUiState.ReadyStatus) return@RestaurantBlock    /// Should I move all this dragging logic to the ViewModel?
                                                //TODO: add a snackbar or some kind of message to notify the user that the restaurant is already selected, so he can't drag another restaurant ("Por favor, presione en el bloque naranja")

                                                draggingRestaurant = restaurant

                                                // Convert DP to PX properly using density.density
                                                val widthPx =
                                                    with(density) { spacing.bigImageSize.toPx() }
                                                val heightPx =
                                                    with(density) { spacing.smallImageSize.toPx() }

                                                // Now the math uses the same "language" (pixels)
                                                grabOffset = Offset(widthPx / 2f, heightPx * 1.5f)

                                                dragPosition = startPos - grabOffset
                                            },
                                            { delta ->
                                                dragPosition += delta
                                            },
                                            onDragEnd = {
                                                val bounds = dropZoneBounds
                                                if (bounds != null) {
                                                    // Calculate the center of the dragged overlay for accurate detection
                                                    val itemWidthPx =
                                                        with(density) { spacing.bigImageSize.toPx() }
                                                    val itemHeightPx =
                                                        with(density) { spacing.smallImageSize.toPx() }
                                                    val centerPosition = dragPosition + Offset(
                                                        itemWidthPx / 2f,
                                                        itemHeightPx / 2f
                                                    )

                                                    if (bounds.contains(centerPosition)) {
                                                        draggingRestaurant?.let {
                                                            viewModel.onRestaurantDropped(it)
                                                        }
                                                    }
                                                }
                                                draggingRestaurant = null
                                            }
                                        )
                                    }
                                }
                            }

//                            Spacer(Modifier.height(spacing.barHeight))
                            ///

                        }
                    }
                }

//                        }
//                        item {
//                            Spacer(Modifier.height(spacing.barHeight))
//                        }
//                    }
                }
                is HomeUiState.Error -> {
                    Text(
                        text = "There was an error",
                        textAlign = TextAlign.Start,
                        color = YummiziTheme.colors.yummiziDarkBlue,
                        style = YummiziTheme.typography.teacherFontVar3
                    )
                }
            }

            // Options part
            NavigationBar(
                Modifier
                    .fillMaxWidth()
                    .height(spacing.barHeight),
//                            .align(Alignment.BottomCenter),
                homeHighlighted = true,
                onYouClicked = { viewModel.onYouClicked() },
                onContactClicked = { viewModel.onContactClicked() },
                // onMoreClicked = { viewModel.onMoreClicked()}  //TODO
            )
        }

        draggingRestaurant?.let { restaurant ->
            DragOverlay(
                restaurant, // TODO: (#919) Change it by a UI model
                dragPosition,
                spacing.bigImageSize,
                spacing.smallImageSize
            )
        }

        YummiziSnackbar(
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, start = 32.dp, end = 32.dp)
        )

        MiniTutorialModal(
            isOpen = miniTutorialUiState.isOpen,
            isNotShowAgainChecked = miniTutorialUiState.isNotShowAgainChecked,
            onDismissRequest = { viewModel.onMiniTutorialClosed() },
            onNotShowAgainChange = { checked -> viewModel.onMiniTutorialNotShowAgainChanged(checked) },
            onCloseClicked = { viewModel.onMiniTutorialClosed() }
        )
    }
}
