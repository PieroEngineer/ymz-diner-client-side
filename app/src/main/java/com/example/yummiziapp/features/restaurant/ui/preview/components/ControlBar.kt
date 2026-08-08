package com.example.yummiziapp.features.restaurant.ui.preview.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.core.ui.components.DiffRoundedCornersRect
import com.example.yummiziapp.R


@Composable
fun ControlBar(
    modifier: Modifier = Modifier,

    onAddRestaurantClicked: () -> Unit
) {
    val spacing = YummiziTheme.spacing

    Box(
        modifier = modifier
            .height(spacing.barHeight*1.6f)
    ){
        DiffRoundedCornersRect(
            modifier = Modifier,
            shadowOn = false,
            topStart = spacing.mediumCornerRadius,
            topEnd = spacing.bigCornerRadius,
            bottomStart = spacing.bigCornerRadius,
            bottomEnd = spacing.mediumCornerRadius
        )

        Row(    // Row containing the buttons
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = spacing.mediumPadding),
            verticalAlignment = Alignment.CenterVertically

        ){
            val iconSize = spacing.bigIconSize
            Spacer(Modifier.weight(1f))

            Image(
                painter = painterResource(R.drawable.add_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(iconSize)
                    .clickable { onAddRestaurantClicked() }
            )
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally
//            ){
//                Image(
//                    painter = painterResource(R.drawable.add_icon),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(iconSize)
//                )
//
//                Spacer(Modifier.height(spacing.tinySeparation))
//
//                Text(
//                    text = "Añadir",
//                    color = YummiziTheme.colors.yummiziWhite,    //TODO: Transparency depend of the selection
//                    style = YummiziTheme.typography.comfortaaFontVar4,
//                    textAlign = TextAlign.Center
//                )
//            }

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .weight(10f)
                    .clip(RoundedCornerShape(spacing.mediumCornerRadius)),
                contentAlignment = Alignment.Center
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(spacing.mediumPadding)
                        .fillMaxWidth()
                ){
                    Image(
                        painter = painterResource(R.drawable.save_order_button),
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize)
                    )
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally
//            ){
//                Image(
//                    painter = painterResource(R.drawable.save_order_button),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(iconSize)
//                )
//
//                Spacer(Modifier.height(spacing.tinySeparation))
//
//                Text(
//                    text = "Pre-escoge\norden",
//                    color = YummiziTheme.colors.yummiziWhite,    //TODO: Transparency depend of the selection
//                    style = YummiziTheme.typography.comfortaaFontVar4,
//                    textAlign = TextAlign.Center
//                )
//            }

                    Spacer(Modifier.weight(3f))

                    Image(
                        painter = painterResource(R.drawable.italic_info_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize)
                    )
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally
//            ){
//                Image(
//                    painter = painterResource(R.drawable.go_back_white_button),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(iconSize)
//                )
//
//                Spacer(Modifier.height(spacing.tinySeparation))
//
//                Text(
//                    text = "Regresar",
//                    color = YummiziTheme.colors.yummiziWhite,    //TODO: Transparency depend of the selection
//                    style = YummiziTheme.typography.comfortaaFontVar4,
//                    textAlign = TextAlign.Center
//                )
//            }

                    Spacer(Modifier.weight(3f))

                    Image(
                        painter = painterResource(R.drawable.share_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize)
                    )
                }

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(YummiziTheme.colors.yummiziBlack.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "Próximamente",
                        color = YummiziTheme.colors.yummiziWhite,
                        style = YummiziTheme.typography.leaguespartanFontVar2
                    )
                }

            }

            Spacer(Modifier.weight(1f))

        }
    }

}