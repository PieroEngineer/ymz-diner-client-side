package com.example.yummiziapp.features.restaurant.ui.preview.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.core.ui.components.DiffRoundedCornersRect
import com.example.yummiziapp.R


@Composable
fun ControlBar(
    modifier: Modifier = Modifier,
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
            val iconSize = spacing.regularIconSize
            Spacer(Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Image(
                    painter = painterResource(R.drawable.add_restauran_button),
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize)
                )

                Spacer(Modifier.height(spacing.tinySeparation))

                Text(
                    text = "Añadir",
                    color = YummiziTheme.colors.yummziziWhite,    //TODO: Transparency depend of the selection
                    style = YummiziTheme.typography.comfortaaFontVar4,
                    textAlign = TextAlign.Center
                )
            }


            Spacer(Modifier.weight(3f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Image(
                    painter = painterResource(R.drawable.save_order_button),
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize)
                )

                Spacer(Modifier.height(spacing.tinySeparation))

                Text(
                    text = "Pre-escoge\norden",
                    color = YummiziTheme.colors.yummziziWhite,    //TODO: Transparency depend of the selection
                    style = YummiziTheme.typography.comfortaaFontVar4,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.weight(3f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Image(
                    painter = painterResource(R.drawable.go_back_white_button),
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize)
                )

                Spacer(Modifier.height(spacing.tinySeparation))

                Text(
                    text = "Regresar",
                    color = YummiziTheme.colors.yummziziWhite,    //TODO: Transparency depend of the selection
                    style = YummiziTheme.typography.comfortaaFontVar4,
                    textAlign = TextAlign.Center
                )
            }


            Spacer(Modifier.weight(1f))

        }
    }

}