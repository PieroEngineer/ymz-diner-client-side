package com.example.yummiziapp.features.home.ui.feed.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.R
import com.example.yummiziapp.core.designsystem.theme.AsyncYummiziImage
import com.example.yummiziapp.core.ui.components.DiffRoundedCornersRect
import com.example.yummiziapp.features.home.ui.feed.ActiveRestaurantsUiState

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
            },
        ){

        Box(    // Second container (Only block)
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ){
            DiffRoundedCornersRect(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.93f),
                topStart = spacing.mediumCornerRadius,
                topEnd = spacing.bigCornerRadius,
                bottomStart = spacing.bigCornerRadius,
                bottomEnd = spacing.mediumCornerRadius
            )

            Row(    // Content
                modifier = Modifier
                    .padding(horizontal = spacing.regularPadding)
                    .fillMaxWidth()
                    .align(Alignment.Center),
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
                            text = if (selectedRestaurants.isNotEmpty()) "Haciendo cola\nen ${selectedRestaurants.size} local${if (selectedRestaurants.size>1) "es" else ""}" else "Mantén presionado\n y arrastra restaurantes\naquí...",
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
                        val readyRestaurant = state.restaurant

                        AsyncYummiziImage(
                            model = readyRestaurant.logoUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .height(spacing.smallImageSize)
                                .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                        )

                        Spacer(Modifier.width(spacing.bigSeparation))

                        Text(   // Information message
                            text = "${readyRestaurant.name} te espera",
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