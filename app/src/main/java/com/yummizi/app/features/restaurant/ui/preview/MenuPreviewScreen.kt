package com.yummizi.app.features.restaurant.ui.preview

import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yummizi.app.core.designsystem.theme.YummiziTheme
import com.yummizi.app.core.ui.components.NavigationBar
import com.yummizi.app.core.ui.components.SearchingArea
import com.yummizi.app.features.restaurant.ui.preview.components.ControlBar
import com.yummizi.app.features.restaurant.ui.preview.components.MenuViewer
import com.yummizi.app.core.ui.components.PageIndicator
import kotlin.uuid.Uuid


//@Preview(showBackground = false)
@Composable
fun MenuPreviewScreen(
    modifier: Modifier = Modifier,

    onFocusedRestaurantSelected: (Uuid) ->Unit,
    onReadyRestaurantSelected: (Uuid) ->Unit,
    onPaidRestaurantSelected: (Uuid) ->Unit,
    onHomeClickedNav: () -> Unit,
    onYouClickedNav: () -> Unit,
    onContactNavigation: () -> Unit,

    viewModel: MenuPreviewViewModel = hiltViewModel()
) {
    val spacing = YummiziTheme.spacing

    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

//    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(Unit) {
        viewModel.navEffect.collect { effect ->
            when(effect) {
                is MenuPreviewUiEvents.NavigateToFocusedRestaurant -> { onFocusedRestaurantSelected(effect.restaurantId) }
                is MenuPreviewUiEvents.NavigateToReadyRestaurantMenu -> { onReadyRestaurantSelected(effect.restaurantId) }
                is MenuPreviewUiEvents.NavigateToPaidRestaurant -> { onPaidRestaurantSelected(effect.restaurantId) }
                is MenuPreviewUiEvents.NavigateToHome -> { onHomeClickedNav() }
                is MenuPreviewUiEvents.NavigateToYou -> {onYouClickedNav() }
                is MenuPreviewUiEvents.NavigateToContact -> { onContactNavigation() }
                is MenuPreviewUiEvents.IntentFindLocation -> {
                    try {
                        uriHandler.openUri(effect.url)
                    } catch (e: Exception) {
                        // Fallback for specific deep links
                        val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                        try {
                            context.startActivity(intent)
                        } catch (anfe: ActivityNotFoundException) {
//                            snackbarHostState.showSnackbar("No se pudo abrir el link")
                            Log.d("MyActivity", "🚩  No se pudo abrir el link")
                        }
                    }
                }
            }
        }
    }

    val menuUiState by viewModel.menuUiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(YummiziTheme.colors.yummiziWhite)
    ) {


        // Searching part
        SearchingArea(
            Modifier
                .padding(spacing.mediumPadding)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Control bar
        ControlBar(
            Modifier
                .fillMaxWidth()
                .padding(spacing.mediumPadding),

            onAddRestaurantClicked = { viewModel.onAddRestaurantClicked() },
            onFindLocationClicked = { viewModel.onFindLocationClicked() }
        )

        Spacer(Modifier.height(spacing.smallSeparation))

        when (val s = menuUiState) {
            is MenuPreviewUiState.Loading -> {
                Text("Wait please")
//                // Show a progress bar so the user knows we are working
//                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    CircularProgressIndicator()
//                }
            }
            is MenuPreviewUiState.Error -> {
                Text(s.message)
            }
            is MenuPreviewUiState.Success -> {
                val menuImages = s.restaurantInfoUiModel.menuImageUrls

                if (menuImages.isNotEmpty()) {
                    // The state that connects both components
                    val pagerState = rememberPagerState(pageCount = { s.restaurantInfoUiModel.menuImageUrls.size })

                    // The Carousel
                    MenuViewer(
                        Modifier.fillMaxWidth(),

                        images = s.restaurantInfoUiModel.menuImageUrls,
                        pagerState = pagerState
                    )

                    Spacer(Modifier.height(spacing.mediumSeparation))

                    // The Indicator (Synced automatically via pagerState)
                    PageIndicator(
                        Modifier,
                        pageCount = s.restaurantInfoUiModel.menuImageUrls.size,
                        pagerState = pagerState
                    )
                }
                else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Este restaurante no tiene menú disponible")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Options part
        NavigationBar(
            Modifier
                .fillMaxWidth()
                .height(spacing.barHeight),
            onHomeClicked = { viewModel.onHomeClicked() },
            onYouClicked = { viewModel.onYouClicked() },
            onContactClicked = { viewModel.onContactClicked() },

            shadowOn = false
        )
    }
}