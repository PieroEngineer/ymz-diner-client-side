package com.example.yummiziapp.features.user.ui.specification.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults.shape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.features.user.ui.model.FocusedRestaurantUiModel
import com.example.yummiziapp.R
import com.example.yummiziapp.core.designsystem.theme.YummiziImage
import com.example.yummiziapp.features.user.ui.model.QueueUiStatus


@Preview(showBackground = true)
@Composable
fun SelectedRestaurant(
    modifier: Modifier = Modifier,

    selectedRestaurant: FocusedRestaurantUiModel = mockupFocusedRestaurant
){
    val spacing = YummiziTheme.spacing

    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    bottomStart = spacing.mediumCornerRadius,
                    bottomEnd = spacing.regularSeparation
                )
            )
            .background(YummiziTheme.colors.yummziziLightOrange2)
            .padding(horizontal = spacing.mediumPadding)
            .padding(vertical = spacing.regularPadding)
    ){
        Column(
            modifier = modifier
                .dropShadow( // Apply the intense drop shadow
                    shape = shape,
                    shadow = Shadow(
                        color = YummiziTheme.colors.yummziziBlack.copy(alpha = .5f),
                        radius = spacing.regularCornerRadius,
                        offset = DpOffset(x = 2.dp, y = 30.dp)
                    )
                )
                .clip(
                    RoundedCornerShape(
                        topStart = spacing.mediumCornerRadius,
                        topEnd = spacing.bigCornerRadius,
                        bottomStart = spacing.bigCornerRadius,
                        bottomEnd = spacing.mediumCornerRadius
                    )
                )
                .background(YummiziTheme.colors.yummziziWhite)
                .padding(horizontal = spacing.mediumPadding)
                .padding(vertical = spacing.tinyPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .padding(top = spacing.mediumPadding)
                    .padding(horizontal = spacing.bigPadding)
            ){  // Restaurant info
                YummiziImage(
                    model = selectedRestaurant.restaurantLogoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(spacing.smallImageSize)
                        .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                )

                Spacer(Modifier.width(spacing.regularSeparation))

                Text(
                    text = "A ${selectedRestaurant.distanceKm}km\n\n${selectedRestaurant.name} espera\nhasta ${selectedRestaurant.maxWaitingTimeInMinutes} min.",
                    textAlign = TextAlign.Start,
                    color = YummiziTheme.colors.yummziziDarkBlue,
                    style = YummiziTheme.typography.teacherFontVar3
                )
            }

            Spacer(Modifier.height(spacing.smallSeparation))

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = spacing.bigPadding),
                thickness = spacing.mediumLineThickness,
                color = YummiziTheme.colors.yummziziDarkOrange
            )

            Spacer(Modifier.weight(2f))

            Column( // Space to set timing
                modifier = Modifier
                    .padding(horizontal = spacing.bigPadding)
            ){
                Text(
                    text = "Tu tiempo máximo de\nllegada",
                    textAlign = TextAlign.Center,
                    color = YummiziTheme.colors.yummziziDarkOrange,
                    style = YummiziTheme.typography.teacherFontVar2
                )

                Spacer(Modifier.height(spacing.mediumPadding))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Row(    /// Maybe convert it into a function, because it's used in other parts too
                        modifier = Modifier
                            .border(
                                BorderStroke(
                                    spacing.mediumLineThickness,
                                    YummiziTheme.colors.yummziziDarkOrange
                                ),
                                shape = RoundedCornerShape(spacing.regularCornerRadius)
                            )
                            .clip(
                                RoundedCornerShape(40)
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ){

                        Image(
                            painter = painterResource(R.drawable.row_btn),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = spacing.mediumPadding)
                                .padding(vertical = spacing.smallPadding)
                                .size(spacing.regularIconSize)
                                .rotate(180f)

                        )

                        Spacer(Modifier.width(spacing.mediumSeparation))

                        Text(
                            text = selectedRestaurant.maxWaitingTimeInMinutes.toString(), //TODO: This start with the max time, but it should be modifiable
                            textAlign = TextAlign.Center,
                            color = YummiziTheme.colors.yummziziDarkOrange,
                            style = YummiziTheme.typography.comfortaaFontVar1,
                            modifier = Modifier
                                .padding(vertical = spacing.smallPadding)
                        )

                        Spacer(Modifier.width(spacing.mediumSeparation))

                        Image(
                            painter = painterResource(R.drawable.row_btn),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = spacing.mediumPadding)
                                .padding(vertical = spacing.smallPadding)
                                .size(spacing.regularIconSize)
                        )
                    }

                    Spacer(Modifier.width(spacing.mediumSeparation))

                    Text(
                        text = "minutos", //TODO: This start with the max time, but it should be modifiable
                        textAlign = TextAlign.Center,
                        color = YummiziTheme.colors.yummziziDarkOrange,
                        style = YummiziTheme.typography.comfortaaFontVar2
                    )
                }
            }

            Spacer(Modifier.weight(2f))

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = spacing.bigPadding),
                thickness = spacing.mediumLineThickness,
                color = YummiziTheme.colors.yummziziDarkOrange
            )

            Spacer(Modifier.height(spacing.smallSeparation))


            Row(    // Request state
                modifier = Modifier
                    .fillMaxWidth(.85f)
                    .padding(bottom = spacing.mediumPadding)
                    .padding(horizontal = spacing.bigPadding)
                    .then(
                        if (selectedRestaurant.waitingState is QueueUiStatus.Accepted) {
                            // If the condition is true, apply the border modifier.
                            Modifier.border(
                                BorderStroke(
                                    spacing.bigLineThickness,
                                    YummiziTheme.colors.yummziziLightOrange2
                                ),
                                shape = RoundedCornerShape(spacing.regularSeparation)
                            )
                        } else {
                            Modifier
                        }
                    )
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.Transparent),
                verticalAlignment = Alignment.CenterVertically
            ){

                Text(
                    text = selectedRestaurant.waitingState.phrase,
                    textAlign = TextAlign.Start,
                    color = YummiziTheme.colors.yummziziLightOrange2,
                    style = YummiziTheme.typography.teacherFontVar1,
                    modifier = Modifier
                        .padding(vertical = spacing.smallSeparation)
                        .padding(start = spacing.regularSeparation)
                )

                Spacer(Modifier.weight(1f))

                when (selectedRestaurant.waitingState) {
                    is QueueUiStatus.Idle -> {}
                    is QueueUiStatus.Queued -> {}
                    is QueueUiStatus.Accepted -> {}
                    is QueueUiStatus.Paid -> {}
                    is QueueUiStatus.Declined -> {}
                }

                if (selectedRestaurant.waitingState.iconRes != 0){
                    Image(
                        painter = painterResource(selectedRestaurant.waitingState.iconRes),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = YummiziTheme.colors.yummziziLightOrange2),
                        modifier = Modifier
                            .size(spacing.smallImageSize)
                            .padding(vertical = spacing.smallSeparation)
                            .padding(end = spacing.regularSeparation)
                    )
                }

            }

        }
    }

}


// A mockup FocusedRestaurantUiModel
val mockupFocusedRestaurant = FocusedRestaurantUiModel(
    restaurantLogoUrl = "https://picsum.photos/200",
    waitingState = QueueUiStatus.Queued,
    distanceKm = 10,
    name = "Restaurante 1",
    maxWaitingTimeInMinutes = 15
)