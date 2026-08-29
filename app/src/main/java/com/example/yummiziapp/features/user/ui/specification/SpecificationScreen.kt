package com.example.yummiziapp.features.user.ui.specification

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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.core.ui.components.CautionConfirmationModal
import com.example.yummiziapp.core.ui.components.DirectAnswerModal
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
    onContactNavigation: () -> Unit,
    onYouClickedNav: () -> Unit,
) {
    val spacing = YummiziTheme.spacing

    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    // Listen for the ViewModel's commands
    LaunchedEffect(Unit) {
        viewModel.navEffect.collect { effect ->
            when (effect) {
                is SpecificationUiEvents.NavigateToRestaurantMenu -> {
                    onRestaurantSelectedNav(effect.restaurantId)
                }

                is SpecificationUiEvents.NavigateToHome -> {
                    onHomeClickedNav()
                }

                is SpecificationUiEvents.NavigateToPreferences -> {
                    onYouClickedNav()
                }

                is SpecificationUiEvents.NavigateToContact -> {
                    onContactNavigation()
                }

                is SpecificationUiEvents.IntentFindLocation -> {
                    try {
                        uriHandler.openUri(effect.url)
                    } catch (e: Exception) {
                        // Fallback for specific deep links
                        val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                        try {
                            context.startActivity(intent)
                        } catch (anfe: ActivityNotFoundException) {
                            Log.d("MyActivity", "🚩  No se pudo abrir el link")
                        }
                    }
                }
            }
        }
    }

    val uiRestaurantState by viewModel.uiRestaurantState.collectAsStateWithLifecycle()
    val uiUserState by viewModel.uiUserState.collectAsStateWithLifecycle()
    var isConfirmationPopupVisible by rememberSaveable { mutableStateOf(false) }
    val restaurantIdentityUiState by viewModel.restaurantIdentityUiState.collectAsStateWithLifecycle()

    var isTryingToCancelRequest by rememberSaveable { mutableStateOf(false) }
    var userIsAskingForModificationInCompanySize by rememberSaveable { mutableStateOf(false) }

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

//X            Spacer(Modifier.height(spacing.regularSeparation))

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

                    mapOf(  /// This is just for testing. The real is a map too, but come from user's backend
                        "is special date" to true,
                        "have baby" to false,
                        "need support" to true
                    )
                )

                when (val state = uiUserState) {    // FIXME: There is a bug that allows user change their compony size while the app is trying to cancel the unique active request. But if there is no internet the request comes back and the company size remains changed. We can fix it by connecting this UI state to the internet connection or state of the request UI.
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
                            onCompanySizeUpdated = { isIncreasing -> viewModel.onCompanySizeUpdated(isIncreasing) },
                            onUserIsAskingForModificationInCompanySize = { userIsAskingForModificationInCompanySize = true }
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

                        SelectionSection(
                            Modifier
                                .padding(horizontal = spacing.mediumPadding)
                                .fillMaxWidth()
                                .zIndex(1f)
                                .weight(1f),

                            listedRestaurants = state.restaurants,

                            onRemoveRestaurant = { id ->
                                viewModel.onTryToRemoveRequest(id)
                                isTryingToCancelRequest = true
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
                                Log.i("MyActivity", "🔎🍽️  Trying to confirm acceptance")

                                isConfirmationPopupVisible = true
                            },

                            onFindLocationClicked = { viewModel.onFindLocationClicked() }
                        )
                    }
                    is RestaurantSpecificationUiState.Error -> {
                        Log.e("MyActivity", "🚩👉  Error when trying to see the specifications: ${state.errorMessage}")
                    }

                }

                Spacer(Modifier.height(spacing.regularSeparation))

                // Navigation part
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

        CautionConfirmationModal(// TODO:  (#741)
            modifier = Modifier,

            isOpen = isTryingToCancelRequest,
            onDismissRequest = { isTryingToCancelRequest = false },

            restaurantImageUrl = restaurantIdentityUiState.imageUrl,
            mainMessage = "¿Deseas cancelar tu solicitud de mesa en\n${restaurantIdentityUiState.name}?",
            noOptimisticButtonText = "Cancelar\nsolicitud",
            optimisticButtonText = "Mantener\nsolicitud",
            complementaryMessage = "Podrás realizar la solicitud nuevamente",

            onNoOptimisticOptionClicked = {
                viewModel.leaveQueue(restaurantIdentityUiState.restaurantId)
                isTryingToCancelRequest = false
            },
            onOptimisticOptionClicked = { isTryingToCancelRequest = false },
        )

        // Insert the Modal at the absolute bottom of the root Box structure
        val focusedRestaurantState = uiRestaurantState
        AcceptanceConfirmationModal(
            isOpen = isConfirmationPopupVisible,
            onDismissRequest = { isConfirmationPopupVisible = false },
            onConfirmationTriggered = {
                // Close the visual window shield immediately
                isConfirmationPopupVisible = false

                // Instruct the ViewModel brain to fire off the background business tasks
                viewModel.onAcceptanceConfirmed()
            },
            maxWaitingTimeInMinutes = if (focusedRestaurantState is RestaurantSpecificationUiState.FocusingRestaurant) focusedRestaurantState.restaurant.customerArrivalTimeInMinutes else 0   // TODO: Change the zero by null and implement it internally
        )

        DirectAnswerModal(
            modifier = Modifier,

            isOpen = userIsAskingForModificationInCompanySize,
            onDismissRequest = { userIsAskingForModificationInCompanySize = false},

            mainMessage = "Mientras haya solicitudes activas,\nno se podrá modificar tu grupo",
            okButtonText = "Entendido",
            complementaryMessage = "Puedes remover las solicitudes",
            onOk = {userIsAskingForModificationInCompanySize = false },
        )

    }

}