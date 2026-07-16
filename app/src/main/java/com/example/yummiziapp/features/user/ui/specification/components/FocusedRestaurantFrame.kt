package com.example.yummiziapp.features.user.ui.specification.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.features.user.ui.model.FocusedRestaurantUiModel
import com.example.yummiziapp.R
import com.example.yummiziapp.core.designsystem.theme.AsyncYummiziImage
import com.example.yummiziapp.features.user.ui.model.RequestUiStatus
import kotlin.uuid.Uuid


//@Preview(showBackground = true)
@Composable
fun FocusedRestaurantFrame(
    modifier: Modifier = Modifier,

    selectedRestaurant: FocusedRestaurantUiModel,

    onQueueJoiningClicked: (Int, Uuid) -> Unit,

    onTryToConfirmAcceptanceClicked: () -> Unit
){
    val spacing = YummiziTheme.spacing

    val maxTimeByRestaurant = selectedRestaurant.maxWaitingTimeInMinutes
    var arrivalTimeInMinutes by rememberSaveable { mutableIntStateOf(maxTimeByRestaurant) }

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
                .padding(vertical = spacing.smallPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .padding(top = spacing.mediumPadding)
                    .padding(horizontal = spacing.bigPadding)
            ){  // Restaurant info
                AsyncYummiziImage(
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

                    NumberSelector(
                        modifier = Modifier,

                        color = YummiziTheme.colors.yummziziDarkOrange,

                        indicatorNumber= arrivalTimeInMinutes,
                        onNumberChanged = { increase ->
                            if (increase) arrivalTimeInMinutes++ else if (arrivalTimeInMinutes > 1) arrivalTimeInMinutes--
                        },
                        maxValue = maxTimeByRestaurant,
                    )

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

            if (selectedRestaurant.waitingState == RequestUiStatus.Queued){
                /// Include a text with "teacher" font with color "yummziziLightOrange2"
                Text(
                    text = "Puedes llegar antes si deseas",
                    textAlign = TextAlign.Center,
                    color = YummiziTheme.colors.yummziziLightOrange2,
                    style = YummiziTheme.typography.teacherFontVar3
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = spacing.bigPadding),
                thickness = spacing.mediumLineThickness,
                color = YummiziTheme.colors.yummziziDarkOrange
            )

            Spacer(Modifier.height(spacing.smallSeparation))


            when (selectedRestaurant.waitingState) {    // Lower area
                is RequestUiStatus.Idle -> {
                    ConfirmationButton(
                        modifier = Modifier
                            .fillMaxWidth(.6f),
                        onClick = {onQueueJoiningClicked(arrivalTimeInMinutes, selectedRestaurant.restaurantId)},
                        text = "Unirse a la cola"
                    )
                }
                is RequestUiStatus.Queued -> {
                    StatusNotice(
                        modifier = Modifier
                            .fillMaxWidth(.85f),
                        selectedRestaurant = selectedRestaurant
                    )
                }
                is RequestUiStatus.Accepted -> {
                    ConfirmationButton(
                        modifier = Modifier
                            .fillMaxWidth(.6f),
                                onClick = { onTryToConfirmAcceptanceClicked() },
                                text = "Elegir\nrestaurante"
                    )
                }
                is RequestUiStatus.Paid -> {
                    StatusNotice(
                        modifier = Modifier
                            .fillMaxWidth(.85f),
                        selectedRestaurant = selectedRestaurant
                    )
                }
                is RequestUiStatus.Declined -> {
                    StatusNotice(
                        modifier = Modifier
                            .fillMaxWidth(.85f),
                        selectedRestaurant = selectedRestaurant
                    )
                }
            }

        }
    }

}


//X A mockup FocusedRestaurantUiModel
//val mockupFocusedRestaurant = FocusedRestaurantUiModel(
//    restaurantLogoUrl = "https://picsum.photos/200",
//    waitingState = RequestUiStatus.Queued,
//    distanceKm = 10,
//    name = "Restaurante 1",
//    maxWaitingTimeInMinutes = 15
//)