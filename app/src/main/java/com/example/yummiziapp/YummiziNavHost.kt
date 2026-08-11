package com.example.yummiziapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yummiziapp.features.auth.ui.authselection.AuthSelectionScreen
import com.example.yummiziapp.features.contact.ui.contact.ContactScreen
import com.example.yummiziapp.features.navigation.YummiziRoute
import com.example.yummiziapp.features.home.ui.feed.HomeScreen
import com.example.yummiziapp.features.payment.ui.arrival.ArrivalScreen
import com.example.yummiziapp.features.payment.ui.checkin.PaymentPreparationScreen
import com.example.yummiziapp.features.restaurant.ui.food.FoodSelectionScreen
import com.example.yummiziapp.features.restaurant.ui.preview.MenuPreviewScreen
import com.example.yummiziapp.features.user.ui.preferences.PreferencesScreen
import com.example.yummiziapp.features.user.ui.profile.ProfileScreen
import com.example.yummiziapp.features.user.ui.specification.SpecificationScreen

@Composable
fun YummiziNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
//        startDestination = YummiziRoute.Home, // 🛡️ Completely type-safe start node
        startDestination = YummiziRoute.AuthSelection
    ) {

        composable<YummiziRoute.AuthSelection> {
            AuthSelectionScreen(
                modifier = modifier,

            )
        }

        composable<YummiziRoute.Home> {
            HomeScreen(
                modifier = modifier,
                onRestaurantSelectionNavigation = { id ->
                    navController.navigate(YummiziRoute.Specification(restaurantId = id?.toString()))
                },
                onRestaurantSelected = { id ->
                    navController.navigate(YummiziRoute.MenuPreview(restaurantId = id.toString()))
                },
                onReadyRestaurantSelected = { id ->
                    navController.navigate(YummiziRoute.FoodSelection(restaurantId = id.toString()))
                },
                onPaidRestaurantSelected = { id ->
                    navController.navigate(YummiziRoute.Arrival(restaurantId = id.toString()))
                },
                onPreferenceNavigation = {
                    navController.navigate(YummiziRoute.Profile)
                },
                onContactNavigation = {
                    navController.navigate(YummiziRoute.Contact)
                }
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
                    navController.navigate(YummiziRoute.Profile)
                },
                onContactNavigation = {
                    navController.navigate(YummiziRoute.Contact)
                }
            )
        }

//        composable<YummiziRoute.Preferences> {
//            PreferencesScreen(modifier = modifier)
//        }

        composable<YummiziRoute.Profile> {
            ProfileScreen(
                modifier = modifier,
                onHomeNavigation = {
                    navController.navigate(YummiziRoute.Home)
                },
                onContactNavigation = {
                    navController.navigate(YummiziRoute.Contact)
                }
            )
        }

        // Jetpack Navigation auto-generates the internal parser bindings!
        composable<YummiziRoute.MenuPreview> {
            MenuPreviewScreen(
                modifier = modifier,
                onFocusedRestaurantSelected = { id ->
                    navController.navigate(YummiziRoute.Specification(restaurantId = id.toString()))
                },
                onReadyRestaurantSelected = { id ->
                    navController.navigate(YummiziRoute.FoodSelection(restaurantId = id.toString()))
                },
                onPaidRestaurantSelected = { id ->
                    navController.navigate(YummiziRoute.Arrival(restaurantId = id.toString()))
                },
                onHomeClickedNav = {
                    navController.navigate(YummiziRoute.Home)
                },
                onYouClickedNav = {
                    navController.navigate(YummiziRoute.Profile)
                },
                onContactNavigation = {
                    navController.navigate(YummiziRoute.Contact)
                }

            )
        }

        composable<YummiziRoute.FoodSelection> {
            FoodSelectionScreen(
                modifier = modifier,
                onHomeNav = {
                    navController.navigate(YummiziRoute.Home)
                },
                onOrderConfirmed = { id ->
                    navController.navigate(YummiziRoute.PaymentPreparation(restaurantId = id.toString()))
                }
            )
        }

        composable<YummiziRoute.PaymentPreparation> {
            PaymentPreparationScreen(
                modifier = modifier,
                onPaymentDoneNav = { id ->
                    navController.navigate(YummiziRoute.Arrival(restaurantId = id.toString()))
                }
            )
        }

        composable<YummiziRoute.Arrival> {
            ArrivalScreen(
                modifier = modifier,
                onHomeClickedNav = {
                    navController.navigate(YummiziRoute.Home)
                }
            )
        }

        composable<YummiziRoute.Contact>{
            ContactScreen(
                onHomeClickedNav = {
                    navController.navigate(YummiziRoute.Home)
                },
                onYouClickedNav = {
                    navController.navigate(YummiziRoute.Profile)
                }
            )
        }
    }
}