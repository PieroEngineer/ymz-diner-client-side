package com.example.yummiziapp.features.restaurant.ui.food

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.features.restaurant.ui.food.components.MenuArea
import com.example.yummiziapp.R
import com.example.yummiziapp.core.ui.components.CautionConfirmationModal
import com.example.yummiziapp.features.payment.ui.arrival.components.RemainingTimeText
import com.example.yummiziapp.features.restaurant.ui.food.components.MenuSettingArea
import com.example.yummiziapp.features.restaurant.ui.model.MenuSetting
import kotlin.uuid.Uuid

//import androidx.compose.ui.tooling.preview.Preview

//@Preview(showBackground = true)
@Composable
fun FoodSelectionScreen(    // TODO: Correct the prop-drilling of this and other screens with the slot solution
    modifier: Modifier = Modifier,
    onHomeNav: () -> Unit,
    onOrderConfirmed: (Uuid) -> Unit,
    viewModel: FoodSelectionViewModel = hiltViewModel()
){
    val spacing = YummiziTheme.spacing

    val menuUiState by viewModel.menuUiState.collectAsStateWithLifecycle()
    val foodSelectionState by viewModel.foodSelectionState.collectAsStateWithLifecycle()
    val totalOrderCost by viewModel.totalOrderCost.collectAsStateWithLifecycle()
    val restaurantIdentityUiState by viewModel.restaurantIdentityUiState.collectAsStateWithLifecycle()

    val selectionFinishedTimestamp by viewModel.selectionFinishedTimestamp.collectAsStateWithLifecycle()


    var isTryingToCancelRequest by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.navEffect.collect { effect ->
            when(effect) {
                is FoodSelectionUiEvents.NavigateToPayment -> { onOrderConfirmed(effect.restaurantId) }
                is FoodSelectionUiEvents.NavigateToHome -> { onHomeNav() }
            }
        }
    }

    if (!isTryingToCancelRequest){ // TODO:  (#741) See in "📱 Kotlin expert 4.0" by "BackHandler(enabled)" how to really manage pop-up frames
        BackHandler{
            isTryingToCancelRequest = true
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        // Screen content
        Column( /// We need to convert it into a function to use it in the preselection too.
            modifier = modifier
                .fillMaxSize()
                .background(YummiziTheme.colors.yummiziWhite)
        ){
            // Professional State Switching
            when (val result = menuUiState) {
                is FoodSelectionUiState.Loading -> {
                    //MenuShimmerLoading() // Your placeholder animation
                    Text(
                        text = "Loading data...",   // TODO: Change this to a loading animation
                        textAlign = TextAlign.Start,
                        color = YummiziTheme.colors.yummiziDarkBlue,
                        style = YummiziTheme.typography.teacherFontVar3
                    )
                }
                is FoodSelectionUiState.Success -> {
                    MenuArea(
                        modifier = Modifier
                            .weight(5f),
                        currentPages = result.pages,
                        onUpdatingSelectionQuantityClicked = { dishId, delta ->
                            viewModel.updateQuantity(dishId, delta)
                        }
                    )
                }
                is FoodSelectionUiState.Error -> {
                    //ErrorView(message = result.message) // A "Retry" button and error text
                    Text(
                        text = "Error: ${result.message}",
                        textAlign = TextAlign.Start,
                        color = YummiziTheme.colors.yummiziDarkBlue,
                        style = YummiziTheme.typography.teacherFontVar3
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(5f)
                    .fillMaxWidth()
                    .paint(
                        painter = painterResource(id = R.drawable.menu_bg),
                        contentScale = ContentScale.FillBounds // Scales the image to fill the bounds
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ){

                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                bottomStart = spacing.mediumCornerRadius,
                                bottomEnd = spacing.mediumCornerRadius
                            )
                        )
                        .background(YummiziTheme.colors.yummiziWhite)
                        .padding(horizontal = spacing.regularPadding)
                        .padding(top = spacing.smallPadding)
                        .fillMaxWidth(.8f),
                    contentAlignment = Alignment.Center
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        RemainingTimeText(
                            targetInstant = selectionFinishedTimestamp,
                            style = YummiziTheme.typography.teacherFontVar6,
                            color = YummiziTheme.colors.yummiziDarkBlue,
                            expiredText = "Prórroga de minutos"
                        )

                        Text(
                            text = " extras",
                            textAlign = TextAlign.Start,
                            color = YummiziTheme.colors.yummiziDarkBlue,
                            style = YummiziTheme.typography.teacherFontVar8
                        )

                        Text(
                            text = " para escoger",
                            textAlign = TextAlign.Start,
                            color = YummiziTheme.colors.yummiziDarkBlue,
                            style = YummiziTheme.typography.teacherFontVar6
                        )

                    }
                }

                val menuSettingForChosenDishes = MenuSetting.CurrentSelections(foodSelectionState)
//            val menuSettingForSavedMenus =    //TODO
//            val menuSettingForPreferences =   //TODO

                MenuSettingArea(    // Amounts control
                    modifier = Modifier
                        .weight(7f),
                    menuStructure = menuSettingForChosenDishes,  //TODO: Later, this parameter will correspond to the list of menu settings to put it in a horizontal pager *2
                    onUpdatingSelectionQuantityClicked = { dishId, delta ->
                        viewModel.updateQuantity(dishId, delta)
                    },
                    onRemoveDishClicked = { dishId ->
                        viewModel.removeDish(dishId)
                    },
                    totalOrderCost = totalOrderCost
                )

                Spacer(Modifier.weight(.1f))

                // Decision buttons
                Row(
                    modifier = Modifier
                        .padding(bottom = spacing.mediumPadding)
                ){

                    Spacer(Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(spacing.mediumCornerRadius)
                            )
                            .background(YummiziTheme.colors.yummiziWhite)
                            .clickable {
                                isTryingToCancelRequest = true  // TODO:  (#741)
                            }
                    ){
                        Text(
                            text = "Cancelar",
                            textAlign = TextAlign.Center,
                            color = YummiziTheme.colors.yummiziBlack,
                            style = YummiziTheme.typography.teacherFontVar2,
                            modifier = Modifier
                                .padding(horizontal = spacing.bigPadding)
                                .padding(vertical = spacing.mediumPadding)
                                .align(Alignment.Center)
                        )
                    }

                    Spacer(Modifier.weight(1.5f))

                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(spacing.mediumCornerRadius)
                            )
                            .background(YummiziTheme.colors.yummiziLightBlue)
                            .clickable {
                                viewModel.confirmOrder()
                            }

                    ){
                        Text(
                            text = "Continuar",
                            textAlign = TextAlign.Center,
                            color = YummiziTheme.colors.yummiziWhite,
                            style = YummiziTheme.typography.teacherFontVar2,
                            modifier = Modifier
                                .padding(horizontal = spacing.bigPadding)
                                .padding(vertical = spacing.mediumPadding)
                                .align(Alignment.Center)
                        )
                    }

                    Spacer(Modifier.weight(1f))
                }
            }
        }

        CautionConfirmationModal(   // TODO:  (#741)
            modifier = Modifier,

            isOpen = isTryingToCancelRequest,
            onDismissRequest= { isTryingToCancelRequest = false },

            restaurantImageUrl = restaurantIdentityUiState.imageUrl,
            mainMessage = "¿Deseas cancelar tu solicitud aceptada de mesa en\n${restaurantIdentityUiState.name}?",
            noOptimisticButtonText = "Cancelar\nsolicitud",
            optimisticButtonText = "Mantener\nsolicitud",
            complementaryMessage = "Podrás entrar a su cola nuevamente",

            onNoOptimisticOptionClicked = viewModel::cancelRequest,
            onOptimisticOptionClicked = { isTryingToCancelRequest = false },
        )

    }

}