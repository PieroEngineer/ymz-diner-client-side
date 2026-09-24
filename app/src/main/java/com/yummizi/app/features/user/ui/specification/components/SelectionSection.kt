package com.yummizi.app.features.user.ui.specification.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.yummizi.app.R
import com.yummizi.app.core.designsystem.theme.AsyncYummiziImage
import com.yummizi.app.core.designsystem.theme.YummiziTheme
import com.yummizi.app.features.user.ui.model.SelectedRestaurantUiModel
import com.yummizi.app.features.user.ui.model.PendingRequestUiStatus
import kotlin.uuid.Uuid


@Composable
fun SelectionSection(
    modifier: Modifier = Modifier,

    listedRestaurants: List<SelectedRestaurantUiModel>,

    onRemoveRestaurant : (Uuid) -> Unit = {},

    onQueuedRestaurantClicked : (Uuid) -> Unit,
    onAcceptedRestaurantClicked : (Uuid) -> Unit,
    onDeclinedRestaurantClicked : (Uuid) -> Unit
){
    val spacing = YummiziTheme.spacing

//X    Log.d("MyActivity", "🔎  'SelectionSection' was called ")

    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    bottomStart = spacing.mediumCornerRadius,
                    bottomEnd = spacing.regularSeparation
                )
            )
            .background(YummiziTheme.colors.yummiziLightOrange2)
            .padding(horizontal = spacing.mediumPadding)
            .padding(vertical = spacing.regularPadding)
    ) {
        Text(   // Its header
            text = "Haciendo cola en ${listedRestaurants.size} locales...",
            color = YummiziTheme.colors.yummiziWhite,
            style = YummiziTheme.typography.leaguespartanFontVar2
        )

        Spacer(Modifier.height(spacing.mediumSeparation))


        if (listedRestaurants.isNotEmpty()) {
            val listState = rememberLazyListState()

            LazyColumn(    // Current selected restaurants
                state = listState,
                verticalArrangement = Arrangement.spacedBy(spacing.mediumSeparation),
            ) {
                items(
                    items = listedRestaurants,
                    key = { selection -> selection.restaurantId }
                ) { restaurantInList ->

                    Row(    // TODO: Convert it in a component
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                when (restaurantInList.waitingState) {  // These are the only states that can be in the list of restaurants in specifications
                                    is PendingRequestUiStatus.Queued -> onQueuedRestaurantClicked(
                                        restaurantInList.restaurantId
                                    )

                                    is PendingRequestUiStatus.Accepted -> onAcceptedRestaurantClicked(
                                        restaurantInList.restaurantId
                                    )

                                    is PendingRequestUiStatus.Declined -> onDeclinedRestaurantClicked(
                                        restaurantInList.restaurantId
                                    )

                                    //Xis PendingRequestUiStatus.Paid -> {} // TODO // This shouldn't be here theoretically, but I place it here just in case so it works even if the screen is bugged

                                    else -> {
                                        Log.d("MyActivity", "🔎🚩  The unexpected waiting status was ${restaurantInList.waitingState}")
                                    }
                                }
                            },

//X                            onRestaurantSelected(restaurantInList.restaurantId, restaurantInList.waitingState) },  /// Should I avoid to pass the waiting state because it's a UI state?
                        verticalAlignment = Alignment.CenterVertically
                    ){
//                        Text( // TODO
//                            text = "A\n${restaurantInList.distanceKm}km",
//                            textAlign = TextAlign.Center,
//                            color = YummiziTheme.colors.yummiziWhite,
//                            style = YummiziTheme.typography.teacherFontVar3
//                        )
//
//                        Spacer(Modifier.width(spacing.mediumSeparation))

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .then(  // Use .then() to conditionally chain modifiers
                                    if (restaurantInList.waitingState is PendingRequestUiStatus.Accepted) {
                                        // If the condition is true, apply the border modifier.
                                        Modifier.border(
                                            BorderStroke(
                                                spacing.bigLineThickness,
                                                YummiziTheme.colors.yummiziWhite
                                            ),
                                            shape = RoundedCornerShape(spacing.mediumCornerRadius)
                                        )
                                    } else {
                                        // If the condition is false, apply no modifier at all.
                                        Modifier // Return an empty, unmodified Modifier
                                    }
                                )
                                .clip(RoundedCornerShape(spacing.mediumCornerRadius)),
                            verticalAlignment = Alignment.CenterVertically
                        ){

                            AsyncYummiziImage(
                                model = restaurantInList.restaurantLogoUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(spacing.smallImageSize)
                                    .padding(vertical = spacing.mediumPadding)
                                    .padding(start = spacing.mediumPadding)
                                    .clip(RoundedCornerShape(30))
                            )

                            Spacer(Modifier.weight(1f))

                            Text(   /// Perhaps it looks better if the text is aligned to the right side
                                text = restaurantInList.waitingState.phrase,
                                textAlign = TextAlign.Start,
                                color = YummiziTheme.colors.yummiziWhite,
                                style = YummiziTheme.typography.teacherFontVar1,
                                modifier = Modifier
                                    .padding(vertical = spacing.mediumPadding)
                            )

                            Spacer(Modifier.weight(1f))

                            Image(
                                painter = painterResource(restaurantInList.waitingState.iconRes),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(spacing.enormousIconSize)
                                    .padding(vertical = spacing.mediumPadding)
                                    .padding(end = spacing.mediumPadding)
                            )

                        }

                        Spacer(Modifier.width(spacing.smallPadding))

                        Image(
                            painter = painterResource(R.drawable.delete_button),
                            contentDescription = null,
                            modifier = Modifier
                                .size(spacing.smallIconSize)
                                .clickable{ onRemoveRestaurant(restaurantInList.restaurantId) }
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Image(
                    painter = painterResource(R.drawable.minitutorial_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                )

                Spacer(Modifier.width(spacing.mediumPadding))

                Text(   /// Perhaps it looks better if the text is aligned to the right side
                    text = "Mantén presionado y arrástra\nun restaurante hacia el bloque superior\npara unirte a la cola",
                    textAlign = TextAlign.Center,
                    color = YummiziTheme.colors.yummiziWhite,
                    style = YummiziTheme.typography.teacherFontVar1
                )

            }
        }


    }
}