package com.example.yummiziapp.features.user.ui.specification.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.features.restaurant.ui.model.SelectedRestaurantUiModel
import com.example.yummiziapp.features.user.ui.model.FocusedRestaurantUiModel
import kotlin.uuid.Uuid

@Composable
fun OptionFrame(    // TODO: Clear the Prop-Drilling
    modifier: Modifier = Modifier,
    onRestaurantSelected: (Uuid) -> Unit = { _ -> },
    onRemoveRestaurant: (Uuid) -> Unit = { _ -> },
    listedRestaurants: List<SelectedRestaurantUiModel> = emptyList(),
    focusedRestaurant: FocusedRestaurantUiModel? = null,

    onQueueJoiningClicked: (Int, Uuid) -> Unit = { _, _ -> }

) {

    val spacing = YummiziTheme.spacing
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


        if (focusedRestaurant != null) {
            Log.d("MyActivity", "🔎📱  Just before Selection section")
            SelectedRestaurantFrame(
                Modifier
                    .padding(horizontal = spacing.mediumPadding)
                    .fillMaxWidth()
                    .zIndex(1f),

                focusedRestaurant,

                onQueueJoiningClicked
            )
        }
        else{
            // "Current queue in restaurants" section
            SelectionSection(
                Modifier
                    .padding(horizontal = spacing.mediumPadding)
                    .fillMaxWidth()
                    .zIndex(1f)
                    .weight(1f),

                listedRestaurants,

                onRemoveRestaurant = { id ->
                    onRemoveRestaurant(id)
                },

                onRestaurantSelected = { id ->
                    onRestaurantSelected(id)

                }
            )
        }

    }
}