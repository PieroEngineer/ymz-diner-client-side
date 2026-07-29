package com.example.yummiziapp.features.restaurant.ui.food

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
import com.example.yummiziapp.features.restaurant.ui.food.components.MenuSettingArea
import com.example.yummiziapp.features.restaurant.ui.model.MenuSetting

//import androidx.compose.ui.tooling.preview.Preview

//@Preview(showBackground = true)
@Composable
fun FoodSelectionScreen(    // TODO: Correct the prop-drilling of this and other screens with the slot solution
    modifier: Modifier = Modifier,
    onOrderConfirmed: () -> Unit,
    viewModel: FoodSelectionViewModel = hiltViewModel()
){
    val spacing = YummiziTheme.spacing

    val menuUiState by viewModel.menuUiState.collectAsStateWithLifecycle()
    val foodSelectionState by viewModel.foodSelectionState.collectAsStateWithLifecycle()
    val totalOrderCost by viewModel.totalOrderCost.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navEffect.collect { effect ->
            when(effect) {
                is FoodSelectionUiEvents.NavigateToPayment -> { onOrderConfirmed() }
            }
        }
    }

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
                    text = "Loading data...",
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
                )
        ){
//            val currentFoodSelected = listOf(foodSelection1, foodSelection2, foodSelection3, foodSelection4, foodSelection5, foodSelection6, foodSelection7)


            // Menu setting

            val menuSettingForChosenDishes = MenuSetting.CurrentSelections(foodSelectionState)
//            val menuSettingForSavedMenus =    //TODO
//            val menuSettingForPreferences =   //TODO

            MenuSettingArea(
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

                ){
                    Text(
                        text = "Atrás",
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

}