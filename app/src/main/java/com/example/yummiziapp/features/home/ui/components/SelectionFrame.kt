package com.example.yummiziapp.features.home.ui.components

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
import com.example.yummiziapp.core.data.local.entities.Restaurant
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.R

@Composable
fun SelectionFrame(
    modifier: Modifier = Modifier,
    selectedRestaurants: List<Restaurant?> = emptyList(),
    onBoundsChanged: (Rect) -> Unit
) {
    val spacing = YummiziTheme.spacing

    Log.d("MyActivity", "🍽️  Current selected restaurants\n$selectedRestaurants\n")

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


                Text(   // Information message
                    text = if (selectedRestaurants.isNotEmpty()) "Haciendo cola\nen ${selectedRestaurants.size} local${if (selectedRestaurants.size>1) "es" else ""}" else "Mantén presionado\n y arrastra restaurantes\naquí...",
                    color = YummiziTheme.colors.yummziziWhite.copy(alpha = if (selectedRestaurants.isNotEmpty()) 1f else .6f),
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