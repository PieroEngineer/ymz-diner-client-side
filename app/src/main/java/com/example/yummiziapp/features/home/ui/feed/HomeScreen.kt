//package com.example.yummiziapp.features.home.ui.feed
//
//import android.util.Log
//import androidx.compose.animation.core.RepeatMode
//import androidx.compose.animation.core.animateFloat
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.core.infiniteRepeatable
//import androidx.compose.animation.core.keyframes
//import androidx.compose.animation.core.rememberInfiniteTransition
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.interaction.collectIsPressedAsState
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.material3.TextFieldDefaults.shape
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.derivedStateOf
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.clipToBounds
//import androidx.compose.ui.draw.drawBehind
//import androidx.compose.ui.draw.dropShadow
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.Rect
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.drawscope.translate
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.graphics.shadow.Shadow
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.layout.onGloballyPositioned
//import androidx.compose.ui.layout.positionInWindow
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.Font
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.DpOffset
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.toSize
//import androidx.compose.ui.zIndex
//import coil3.compose.AsyncImage
//import coil3.compose.rememberAsyncImagePainter
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import kotlin.uuid.ExperimentalUuidApi
//import kotlin.uuid.Uuid
//
////@Preview(showBackground = true)
//@Composable
//fun HomeScreen(
//    modifier: Modifier = Modifier,
//    onRestaurantSelectionNavigation: () -> Unit,
//    onRestaurantSelected: (Uuid) -> Unit,
//    onPreferenceNavigation: () -> Unit,
//    viewModel: HomeViewModel = hiltViewModel()
//) {
//
//    // Listen for the ViewModel's commands
//    LaunchedEffect(Unit) {
//        viewModel.navEffect.collect { effect ->
//            when (effect) {
//                is HomeScreenUiEvents.NavigateToRestaurantSelectionFrame -> { onRestaurantSelectionNavigation() }
//                is HomeScreenUiEvents.NavigateToRestaurantMenuPreview -> { onRestaurantSelected(effect.restaurantId) }
//                is HomeScreenUiEvents.NavigateToPreferences -> { onPreferenceNavigation() }
//
//                is HomeScreenUiEvents.ShowConnectionError -> { /* ... */ }
//            }
//        }
//    }
//
//    val spacing = YummiziTheme.spacing
//
//
//    // State variables
//    var draggingRestaurant by remember { mutableStateOf<Restaurant?>(null) }
//    var dragPosition by remember { mutableStateOf(Offset.Zero) }
//    var grabOffset by remember { mutableStateOf(Offset.Zero) }
//    var dropZoneBounds by remember { mutableStateOf<Rect?>(null) }
//    val interactionSource = remember { MutableInteractionSource() }
//    val isPressed by interactionSource.collectIsPressedAsState()
//
//    val selectedRestaurants by viewModel.pendingRestaurants.collectAsStateWithLifecycle()    // This state determine what restaurants are in the orange frame
//    val uiState by viewModel.uiState.collectAsState()
//
//
//    // Animate scale: 0.9f when pressed, 1f when released
//    val scale by animateFloatAsState(
//        targetValue = if (isPressed) 0.9f else 1f,
//        label = "scaleAnimation"
//    )
//
//    val density = LocalDensity.current
//
//    // Screen content
//    Box(
//        modifier = modifier.fillMaxSize()
//    ){  // Main container
//        Column( // Main column
//            modifier = Modifier
//                .fillMaxSize()
//                .align(Alignment.TopCenter),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ){
//
//            // Searching part
//            SearchingArea(
//                Modifier
//                    .padding(spacing.mediumPadding)
//            )
//
//            Spacer(Modifier.height(spacing.mediumSeparation))
//
//            // Frame part
//            SelectionFrame(
//                modifier = Modifier
//                    .height(spacing.frameHeight)
//                    .graphicsLayer {
//                        scaleX = scale
//                        scaleY = scale
//                    }
//                    .padding(horizontal = spacing.mediumPadding)
//                    .clickable(
//                        interactionSource = interactionSource,
//                        indication = null, // Removes the ripple
//                        onClick = { viewModel.onSelectionFrameClicked() }
//                    ),
//
//                selectedRestaurants = selectedRestaurants,
//                onBoundsChanged = { bounds ->
//                    dropZoneBounds = bounds
//                }
//            )
//
//            Spacer(Modifier.height(spacing.regularSeparation))
//
//            // Visual separator
//            DiffRoundedCornersRect(
//                Modifier
//                    .height(spacing.mediumSeparation)
//                    .fillMaxWidth(),
//
//                topStart = spacing.tinyCornerRadius,
//                topEnd = spacing.tinyCornerRadius,
//                bottomStart = 0.dp,
//                bottomEnd = 0.dp
//            )
//
//            // Section part
//            when (val state = uiState) {
//                is HomeUiState.Loading -> {
//                    Text(
//                        text = "Loading data...",
//                        textAlign = TextAlign.Start,
//                        color = YummiziTheme.colors.yummziziDarkBlue,
//                        style = YummiziTheme.typography.teacherFontVar3
//                    )
//                }
//                is HomeUiState.Success -> {
//                    SectionArea(
//                        Modifier
//                            .weight(1f)
//                            .background(YummiziTheme.colors.yummziziWhite),
//
//                        sections = state.sections,   // Receive the list of sections from the viewModel
//
//                        onRestaurantClicked = { id ->
//                            viewModel.onRestaurantClicked(id)
//                        },
//
//                        onDragStart = { restaurant, startPos ->
//                            draggingRestaurant = restaurant
//
//                            // Convert DP to PX properly using density.density
//                            val widthPx = with(density) { spacing.bigImageSize.toPx() }
//                            val heightPx = with(density) { spacing.smallImageSize.toPx() }
//
//                            // Now the math uses the same "language" (pixels)
//                            grabOffset = Offset(widthPx / 2f, heightPx*1.5f)
//
//                            dragPosition = startPos - grabOffset
//                        },
//
//                        onDrag = { delta ->
//                            dragPosition += delta
//                        }
//                    ) {
//                        val bounds = dropZoneBounds
//                        if (bounds != null && bounds.contains(dragPosition)) {
//                            draggingRestaurant?.let {
//
//                                if (it != null){
//                                    viewModel.onRestaurantDropped(it)
//                                }
//                            }
//                        }
//                        draggingRestaurant = null
//                    }
//                }
//                is HomeUiState.Error -> {
//                    Text(
//                        text = "There was an error",
//                        textAlign = TextAlign.Start,
//                        color = YummiziTheme.colors.yummziziDarkBlue,
//                        style = YummiziTheme.typography.teacherFontVar3
//                    )
//                }
//            }
//
//
//
//        }
//
//        draggingRestaurant?.let { restaurant ->
//            DragOverlay(restaurant, dragPosition, spacing.bigImageSize, spacing.smallImageSize)
//        }
//
//        // Options part
//        NavigationBar(
//            Modifier
//                .fillMaxWidth()
//                .height(spacing.barHeight)
//                .align(Alignment.BottomCenter),
//            homeHighlighted = true,
//            onYouClicked = { viewModel.onYouClicked() },
//            // onMoreClicked = { viewModel.onMoreClicked()}  //TODO
//        )
//
//    }
//}