package com.example.yummiziapp.features.user.ui.specification.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import androidx.compose.ui.res.painterResource
import com.example.yummiziapp.R

@Composable
fun SpecificationSection(
    modifier: Modifier = Modifier,

    selectedOptions: Map<String, Boolean>
) {
    val spacing = YummiziTheme.spacing

    Box(
        modifier = modifier
    ){  // Container

        Box(    // Connection effect
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(spacing.bigCornerRadius)    // Connect with the rounded corner
                .fillMaxWidth()
                .background(YummiziTheme.colors.yummziziDarkOrange)
        )

        Column(
            modifier = Modifier
                .border(
                    BorderStroke(spacing.bigLineThickness, YummiziTheme.colors.yummziziWhite),
                    shape = RoundedCornerShape(
                        topStart = spacing.mediumCornerRadius,
                        topEnd = spacing.bigCornerRadius,
                        bottomStart = spacing.bigCornerRadius,
                        bottomEnd = spacing.mediumCornerRadius
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
                .background(YummiziTheme.colors.yummziziDarkOrange)
        ) {

            Row(    // Its header
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.regularPadding)
                    .padding(horizontal = spacing.mediumPadding)
            ) {
                Text(
                    text = "Tus especificaciones",
                    color = YummiziTheme.colors.yummziziWhite,
                    style = YummiziTheme.typography.leaguespartanFontVar2
                )

                Spacer(Modifier.weight(1f))

                Image(
                    painter = painterResource(R.drawable.no_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(spacing.regularIconSize)
                )

                Spacer(Modifier.width(spacing.bigLineThickness))

                Image(
                    painter = painterResource(R.drawable.yes_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(spacing.regularIconSize)
                )

                Spacer(Modifier.weight(2f))
            }

            Spacer(Modifier.height(spacing.mediumSeparation))

            Row(    // Available selections
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.mediumPadding)
                    .padding(bottom = spacing.regularPadding)
            ) {

                Spacer(Modifier.weight(1f))

                Column(  // Special date switch
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Image(
                        painter = painterResource(R.drawable.celebration_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(spacing.reducedImageSize)
                            .clip(CircleShape)
                            .background(
                                if (selectedOptions["is special date"]
                                        ?: false
                                ) YummiziTheme.colors.yummziziLightOrange2 else YummiziTheme.colors.yummziziGray
                            )
                            .border(
                                BorderStroke(
                                    spacing.mediumLineThickness,
                                    YummiziTheme.colors.yummziziWhite
                                ),
                                CircleShape
                            )
                    )

                    Text(
                        text = "Día especial",
                        color = YummiziTheme.colors.yummziziWhite,
                        style = YummiziTheme.typography.teacherFontVar6
                    )
                }

                Spacer(Modifier.weight(2.5f))

                Column(  // Baby included switch
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Image(
                        painter = painterResource(R.drawable.baby_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(spacing.reducedImageSize)
                            .clip(CircleShape)
                            .background(
                                if (selectedOptions["have baby"]
                                        ?: false
                                ) YummiziTheme.colors.yummziziLightOrange2 else YummiziTheme.colors.yummziziGray
                            )
                            .border(
                                BorderStroke(
                                    spacing.mediumLineThickness,
                                    YummiziTheme.colors.yummziziWhite
                                ),
                                CircleShape
                            )
                    )

                    Text(
                        text = "Con bebé",
                        color = YummiziTheme.colors.yummziziWhite,
                        style = YummiziTheme.typography.teacherFontVar6
                    )
                }

                Spacer(Modifier.weight(2.5f))

                Column(  // Support needed switch
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Image(
                        painter = painterResource(R.drawable.support_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(spacing.reducedImageSize)
                            .clip(CircleShape)
                            .background(
                                if (selectedOptions["need support"]
                                        ?: false
                                ) YummiziTheme.colors.yummziziLightOrange2 else YummiziTheme.colors.yummziziGray
                            )
                            .border(
                                BorderStroke(
                                    spacing.mediumLineThickness,
                                    YummiziTheme.colors.yummziziWhite
                                ),
                                CircleShape
                            )
                    )

                    Text(
                        text = "Apoyo",
                        color = YummiziTheme.colors.yummziziWhite,
                        style = YummiziTheme.typography.teacherFontVar6
                    )
                }

                Spacer(Modifier.weight(1f))


            }
        }

    }
}