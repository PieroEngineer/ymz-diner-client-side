package com.yummizi.app.features.home.ui.feed.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.toSize
import com.yummizi.app.core.designsystem.theme.YummiziTheme
import com.yummizi.app.R
import com.yummizi.app.core.data.local.model.RequestStatusToken
import com.yummizi.app.core.designsystem.theme.AsyncYummiziImage
import com.yummizi.app.features.home.ui.feed.ActiveRestaurantsUiState

@Composable
fun SelectionFrame(
    modifier: Modifier = Modifier,
    activeRestaurantsUiState: ActiveRestaurantsUiState,
    onBoundsChanged: (Rect) -> Unit
) {
    val spacing = YummiziTheme.spacing


    Box(    // First Container (Block + logo)
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                val position = coordinates.positionInWindow()
                val size = coordinates.size.toSize()
                onBoundsChanged(Rect(position, size))
            }
    ){

        Column( // Container (Main content + bellow border)
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.93f)
                .align(Alignment.BottomCenter)
        ){
            Box(    // Container (Main content + connection of bellow border)
                modifier = Modifier
                    .weight(1f)
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.bigCornerRadius)
                        .background(YummiziTheme.colors.yummiziDarkOrange)
                        .align(Alignment.BottomCenter)
                )

                Row(    // Main content
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = spacing.mediumCornerRadius,
                                topEnd = spacing.bigCornerRadius,
                                bottomStart = spacing.bigCornerRadius,
                                bottomEnd = spacing.mediumCornerRadius
                            )
                        )
                        .background(YummiziTheme.colors.yummiziLightOrange2)
                        .padding(horizontal = spacing.regularPadding)
                        .fillMaxSize()
                        .align(Alignment.BottomCenter),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    when (val state = activeRestaurantsUiState) {
                        is ActiveRestaurantsUiState.Loading -> {
                            Text(
                                text = "Loading data...",
                                color = YummiziTheme.colors.yummiziDarkBlue,
                                style = YummiziTheme.typography.teacherFontVar3
                            )
                        }
                        is ActiveRestaurantsUiState.PendingStatus -> {
                            val selectedRestaurants = state.restaurants

                            Text(   // Information message
                                text = if (selectedRestaurants.isNotEmpty()) "Haciendo cola\nen ${selectedRestaurants.size} local${if (selectedRestaurants.size>1) "es" else ""}" else "Soltar restaurantes\nsolcitados aquí",
                                color = YummiziTheme.colors.yummiziWhite.copy(alpha = if (selectedRestaurants.isNotEmpty()) 1f else .6f),
                                style = YummiziTheme.typography.teacherFontVar2
                            )

                            Spacer(Modifier.width(spacing.bigSeparation))

                            if(selectedRestaurants.isNotEmpty()){    // With restaurants -> Restaurant logos

                                StackImagesHorizontally(
                                    Modifier
                                        .height(spacing.smallImageSize)
                                        .fillMaxWidth(),

                                    selectedRestaurants.mapNotNull{it?.logoUrl}
                                )

                            }
                            else{    // Without restaurants -> Placeholder image
                                Image(
                                    painter = painterResource(R.drawable.placeholder_select),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(spacing.mediumImageSize)
                                )
                            }
                        }
                        is ActiveRestaurantsUiState.ReadyStatus ->{
                            val readyRestaurantWithStatus = state.restaurantWithStatus

                            AsyncYummiziImage(
                                model = readyRestaurantWithStatus.restaurant.logoUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .height(spacing.smallImageSize)
                                    .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                            )

                            Spacer(Modifier.width(spacing.bigSeparation))

                            Text(   // Information message
                                text = if (readyRestaurantWithStatus.requestStatus == RequestStatusToken.PAID) "Por favor, compartenos tu\núltima experiencia :D" else "${readyRestaurantWithStatus.restaurant.name} te espera",
                                color = YummiziTheme.colors.yummiziWhite,
                                style = YummiziTheme.typography.teacherFontVar2
                            )
                        }
                        is ActiveRestaurantsUiState.Error -> {
                            Log.d("MyActivity", "🚩  There was an error when displaying the restaurants in the orange frame...\n")
                        }
                    }
                }
            }

            Box(    // Container (Bellow border)
                modifier = Modifier
//                    .defaultMinSize(minHeight = spacing.bigCornerRadius)
                    .clip(
                        RoundedCornerShape(
                            bottomStart = spacing.bigCornerRadius,
                            bottomEnd = spacing.mediumCornerRadius
                        )
                    )
                    .fillMaxWidth()
                    .background(YummiziTheme.colors.yummiziDarkOrange)
            ){
                Text(
                    text = "Presiona aquí",
                    color = YummiziTheme.colors.yummiziWhite,
                    style = YummiziTheme.typography.teacherFontVar3,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }

        Image(
            painter = painterResource(R.drawable.yummizi_logo),
            contentDescription = null,
            modifier = Modifier
                .size(spacing.reducedImageSize)
                .clip(RoundedCornerShape(spacing.mediumCornerRadius * .75f))
                .align(Alignment.TopStart)
        )
    }
}