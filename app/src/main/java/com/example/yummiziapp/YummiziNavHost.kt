package com.example.yummiziapp

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yummiziapp.features.navigation.YummiziRoute
import com.example.yummiziapp.features.home.ui.feed.HomeScreen
import com.example.yummiziapp.features.payment.ui.beforepay.PaymentPreparationScreen
import com.example.yummiziapp.features.restaurant.ui.food.FoodSelectionScreen
import com.example.yummiziapp.features.restaurant.ui.preview.MenuPreviewScreen
import com.example.yummiziapp.features.user.ui.preferences.PreferencesScreen
import com.example.yummiziapp.features.user.ui.specification.SpecificationScreen

@Composable
fun YummiziNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = YummiziRoute.Home // 🛡️ Completely type-safe start node
    ) {

        composable<YummiziRoute.Home> {
            HomeScreen(
                modifier = modifier,
                onRestaurantSelectionNavigation = { id ->
                    Log.d("MyActivity", "🔎🪪  Is there an ID? $id")
                    navController.navigate(YummiziRoute.Specification(restaurantId = id?.toString()))
                    Log.d("MyActivity", "🔎📌  Checkpoint after navigation")
                },
                onRestaurantSelected = { id ->
                    navController.navigate(YummiziRoute.MenuPreview(restaurantId = id.toString()))
                },
                onReadyRestaurantSelected = { id ->
                    navController.navigate(YummiziRoute.FoodSelection(restaurantId = id.toString()))
                },
                onPreferenceNavigation = {
                    navController.navigate(YummiziRoute.Preferences)
                },
                //X
//                onRestaurantSelectedNav = { id ->
//                    navController.navigate(YummiziRoute.FoodSelection(restaurantId = id.toString()))
//                },
            )
        }

        composable<YummiziRoute.Specification> {
            SpecificationScreen(
                modifier = modifier,
                onRestaurantSelectedNav = { id ->
                    navController.navigate(YummiziRoute.FoodSelection(restaurantId = id.toString()))
                },
                onHomeClickedNav = {
                    // Pops back safely or routes cleanly
                    navController.popBackStack()
                },
                onYouClickedNav = {
                    navController.navigate(YummiziRoute.Preferences)
                }
            )
        }

        composable<YummiziRoute.Preferences> {
            PreferencesScreen(modifier = modifier)
        }

        // Jetpack Navigation auto-generates the internal parser bindings!
        composable<YummiziRoute.MenuPreview> {
            MenuPreviewScreen()
        }

        composable<YummiziRoute.FoodSelection> {
            FoodSelectionScreen(
                onOrderConfirmed = {
                    navController.navigate(YummiziRoute.PaymentPreparation)
                }
            )
        }

        composable<YummiziRoute.PaymentPreparation> {
            PaymentPreparationScreen()
        }
    }
}