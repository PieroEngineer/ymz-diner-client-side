package com.yummizi.app.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.yummizi.app.core.designsystem.theme.YummiziTheme
import com.yummizi.app.R

@Composable
fun NavigationBar(  //TODO: Change the visual part for the other nice style (From newmie)
    modifier: Modifier = Modifier,

    homeHighlighted: Boolean = false,
    youHighlighted: Boolean = false,
    moreHighlighted: Boolean = false,

    onHomeClicked: () -> Unit = {},
    onYouClicked: () -> Unit = {},
    onContactClicked: () -> Unit = {},

    shadowOn: Boolean = true

) {
    val spacing = YummiziTheme.spacing

    Box(    // White box
        modifier = modifier
            .dropShadow( // Apply the intense drop shadow
                shape = CircleShape,
                shadow = Shadow(
                    color = YummiziTheme.colors.yummiziWhite.copy(alpha = if (shadowOn) .9f else 0f),
                    radius = 20.dp,                          // High radius for a wide, soft spread
                    offset = DpOffset(x = 0.dp, y = (-18).dp), // Offset only to the bottom
                )
            )
//            .background(color = YummiziTheme.colors.yummziziWhite)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Row(    // Blue box
            modifier = Modifier
                .fillMaxWidth(.95f)
                .fillMaxHeight()
                //.clip(RoundedCornerShape(spacing.regularPaging))
                .clip(RoundedCornerShape(18.dp))
                .background(YummiziTheme.colors.yummiziDarkBlue),
            verticalAlignment = Alignment.CenterVertically

        ){
            Spacer(Modifier.weight(1f))

            Column( // Home button
                modifier = Modifier
                    .padding(vertical = spacing.smallPadding)
                    .clickable { onHomeClicked() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.home_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(spacing.mediumIconSize),
                    alpha = if (homeHighlighted) 1.0f else .5f
                )

                Spacer(Modifier.height(spacing.mediumLineThickness))

                Text(
                    text = "Home",
                    color = YummiziTheme.colors.yummiziWhite,    //TODO: Transparency depend of the selection
                    style = YummiziTheme.typography.teacherFontVar4
                )
            }

            Spacer(Modifier.weight(3f))

            Column( // You Icon
                modifier = Modifier
                    .padding(vertical = spacing.smallPadding)
                    .clickable { onYouClicked() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.you_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(spacing.mediumIconSize),
                    alpha = if (youHighlighted) 1.0f else .5f
                )

                Spacer(Modifier.height(spacing.tinySeparation))

                Text(
                    text = "You",
                    color = YummiziTheme.colors.yummiziWhite,
                    style = YummiziTheme.typography.teacherFontVar4
                )
            }

            Spacer(Modifier.weight(3f))

            Column( // More Icon
                modifier = Modifier
                    .padding(vertical = spacing.smallPadding)
                    .clickable { onContactClicked() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.cloud_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(spacing.mediumIconSize),
                    alpha = if (moreHighlighted) 1.0f else .5f
                )

                Spacer(Modifier.height(spacing.tinySeparation))

                Text(
                    text = "Contact",
                    color = YummiziTheme.colors.yummiziWhite,
                    style = YummiziTheme.typography.teacherFontVar4
                )
            }

            Spacer(Modifier.weight(1f))

        }
    }
}