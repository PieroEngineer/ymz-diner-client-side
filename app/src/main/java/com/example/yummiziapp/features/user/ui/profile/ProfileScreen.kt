package com.example.yummiziapp.features.user.ui.profile

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.R
import com.example.yummiziapp.core.ui.components.NavigationBar
import com.example.yummiziapp.core.ui.components.SquareButton

//@Preview( showBackground = true)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onHomeNavigation: () -> Unit = {},
    onContactNavigation: () -> Unit = {},

    viewModel: ProfileViewModel = hiltViewModel()
){
    val spacing = YummiziTheme.spacing

    LaunchedEffect(Unit) {
        viewModel.navEffect.collect { effect ->
            when(effect) {
                is ProfileUiEvent.NavigateToHome -> { onHomeNavigation() }
                is ProfileUiEvent.NavigateToContact -> { onContactNavigation() }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(YummiziTheme.colors.yummiziWhite),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Box(    // Image container
            modifier = Modifier
                .fillMaxWidth()
        ){

            Box(
                modifier = Modifier
                    .fillMaxWidth()
//                    .fillMaxHeight(.5f)
                    .height(spacing.bigCornerRadius)
                    .background(YummiziTheme.colors.yummiziLightOrange)
                    .align(Alignment.BottomCenter)
            )

            Image(
                painter = painterResource(R.drawable.profile_placeholder),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.FillWidth
            )
        }

        Box(    // Container (Bellow border with name)
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        bottomStart = spacing.bigCornerRadius,
                        bottomEnd = spacing.bigCornerRadius
                    )
                )
                .fillMaxWidth()
                .background(YummiziTheme.colors.yummiziLightOrange)
                .padding(vertical = spacing.regularPadding)
        ){
            Text(
                text = "Alberta K.",
                color = YummiziTheme.colors.yummiziWhite,
                style = YummiziTheme.typography.leaguespartanFontVar6,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable{
                        // TODO: Allow user to modifiy its name
                    }
            )
        }

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .padding(horizontal = spacing.regularPadding),
            contentAlignment = Alignment.Center
        ){

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Box(    // Top button
                    modifier = Modifier
                        .fillMaxWidth()
                        .dropShadow( /// Apply the intense drop shadow
                            shape = RoundedCornerShape(spacing.mediumCornerRadius),
                            shadow = Shadow(
                                color = YummiziTheme.colors.yummiziBlack,
                                radius = 30.dp,                        // High radius for a wide, soft spread
                                offset = DpOffset(x = 15.dp, y = 20.dp), // Offset only to the bottom
                            )
                        )
                        .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = List(4){YummiziTheme.colors.yummiziLightOrange} + YummiziTheme.colors.yummiziYellow
                            )
                        ),
                    contentAlignment = Alignment.Center
                ){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = spacing.bigPadding, horizontal = spacing.regularPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Image(
                            painter = painterResource(R.drawable.people_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(spacing.hugeIconSize)
                        )

                        Spacer(Modifier.width(spacing.mediumSeparation))

                        Text(
                            text = "Comunidad",
                            color = YummiziTheme.colors.yummiziWhite,
                            style = YummiziTheme.typography.teacherFontVar7,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clickable{
                                    // TODO: Allow user to modifiy its name
                                }
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    SquareButton(
                        modifier = Modifier
                            .weight(1f)
                            .dropShadow( /// Apply the intense drop shadow
                                shape = RoundedCornerShape(spacing.mediumCornerRadius),
                                shadow = Shadow(
                                    color = YummiziTheme.colors.yummiziBlack,
                                    radius = 30.dp,                        // High radius for a wide, soft spread
                                    offset = DpOffset(x = 15.dp, y = 20.dp), // Offset only to the bottom
                                )
                            ),
                        ownImageId = R.drawable.save_button,
                        ownText = "A tu estilo",
                        textColor = YummiziTheme.colors.yummiziLightOrange,
                        iconColor = YummiziTheme.colors.yummiziLightBlue2,
                        textStyle = YummiziTheme.typography.teacherFontVar5

                    )

                    SquareButton(
                        modifier = Modifier
                            .weight(1f)
                            .dropShadow( /// Apply the intense drop shadow
                                shape = RoundedCornerShape(spacing.mediumCornerRadius),
                                shadow = Shadow(
                                    color = YummiziTheme.colors.yummiziBlack,
                                    radius = 30.dp,                        // High radius for a wide, soft spread
                                    offset = DpOffset(x = 15.dp, y = 20.dp), // Offset only to the bottom
                                )
                            ),
                        ownImageId = R.drawable.timing_icon,
                        ownText = "Historia",
                        textColor = YummiziTheme.colors.yummiziLightOrange,
                        iconColor = YummiziTheme.colors.yummiziLightBlue2,
                        textStyle = YummiziTheme.typography.teacherFontVar5
                    )
                }
            }

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                    .background(YummiziTheme.colors.yummiziBlack.copy(alpha = 0.8f)),
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

        NavigationBar(
            Modifier
                .fillMaxWidth()
                .height(spacing.barHeight),

            youHighlighted = true,

            onHomeClicked = { viewModel.onHomeClicked() },
            onContactClicked = { viewModel.onContactClicked() },

            shadowOn = false,
        )
    }
}