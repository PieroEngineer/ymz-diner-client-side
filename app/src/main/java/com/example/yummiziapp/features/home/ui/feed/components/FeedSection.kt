package com.example.yummiziapp.features.home.ui.feed.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.R

//@Preview(showBackground = true)
@Composable
fun FeedSection(
    modifier: Modifier = Modifier,

    title: String = "Sección aquí",

    contentSlot: @Composable () -> Unit
){
    val spacing = YummiziTheme.spacing
    Column(
        modifier = modifier
    ){
        Row(    // Section header
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.mediumPadding),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = title,
                style = YummiziTheme.typography.leaguespartanFontVar3,
                color = YummiziTheme.colors.yummiziBlack
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .height(spacing.mediumLineThickness * .75f)
                    .padding(horizontal = spacing.mediumPadding)
                    .background(YummiziTheme.colors.yummiziBlack)
            )

            Row(
                modifier = Modifier
                //.height(spacing.hugeSeparation)  /// Should wrap the content
                /// clickable
            ){  // "More" button
                Text(
                    text = "Más",
                    style = YummiziTheme.typography.comfortaaFontVar3,
                    color = YummiziTheme.colors.yummiziGray2
                )

                Spacer(Modifier.width(spacing.smallSeparation))

                Image(
                    painter = painterResource(R.drawable.more_button),
                    contentDescription = null,
                    modifier = Modifier
                        .size(spacing.regularIconSize)
                )
            }
        }

        Spacer(Modifier.height(spacing.tinySeparation))

        contentSlot()
//X        RestaurantScrollable(
//            Modifier,
//
//            restaurants,
//            blockHeight,
//
//            containerSectionId,
//            reelActivated,
//            onScrollUsed,
//
//            onDragStart,
//            onDrag,
//            onDragEnd,
//
//            onRestaurantClicked
//        )

    }
}