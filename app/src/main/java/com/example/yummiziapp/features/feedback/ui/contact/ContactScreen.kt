package com.example.yummiziapp.features.feedback.ui.contact

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.yummiziapp.R
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.yummiziapp.core.ui.components.NavigationBar

//@Preview()
@Composable
fun ContactScreen(
    modifier: Modifier = Modifier,

    onHomeClickedNav: () -> Unit,
    onYouClickedNav: () -> Unit,

    viewModel: ContactViewModel = hiltViewModel()
){
    val spacing = YummiziTheme.spacing

    LaunchedEffect(Unit) {
        viewModel.navEffect.collect { effect ->
            when (effect) {
                is ContactUiEvent.NavigateToHome -> { onHomeClickedNav() }
                is ContactUiEvent.NavigateToPreferences -> { onYouClickedNav() }
            }
        }
    }

    Box(
        modifier = modifier
    ){
        Image(
            painter = painterResource(id = R.drawable.contact_bg),
            contentDescription = null, // Backgrounds are decorative
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }

    Column(     // Main column
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.regularPadding)
    ){

        Image(
            painter = painterResource(R.drawable.yummizi_logo),
            contentDescription = null,
            modifier = Modifier
                .size(spacing.smallImageSize)
                .clip(RoundedCornerShape(spacing.smallCornerRadius))
        )

        Spacer(Modifier.height(spacing.regularSeparation))

        Text(
            text = "Mantengamos\ncontacto",
            color = YummiziTheme.colors.yummiziDarkBlue2,
            style = YummiziTheme.typography.leaguespartanFontVar6,
            modifier = Modifier
                .background(
                    color = YummiziTheme.colors.yummiziWhite,
                    shape = RoundedCornerShape(spacing.smallCornerRadius)
                )
                .padding(spacing.mediumPadding)
        )

        Spacer(Modifier.height(spacing.mediumSeparation))

        Text(
            text = "¿Qué deseas comunicar?",
            color = YummiziTheme.colors.yummiziDarkBlue2,
            style = YummiziTheme.typography.teacherFontVar3,
            modifier = Modifier
                .background(
                    color = YummiziTheme.colors.yummiziWhite,
                    shape = RoundedCornerShape(spacing.tinyCornerRadius)
                )
                .padding(spacing.smallSeparation)
        )

        Spacer(Modifier.weight(3f))


        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ){
            RectangleButton(
                ownImageId = R.drawable.conversation_icon,
                ownText = "Tengo\ncomentarios",
            )

            Spacer(Modifier.width(spacing.mediumSeparation))

            RectangleButton(
                ownImageId = R.drawable.handshake_icon,
                ownText = "Probar en mi\nrestaurante",
            )
        }

        Spacer(Modifier.height(spacing.mediumSeparation))

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ){
            Spacer(Modifier.weight(1f))

            RectangleButton(
                ownImageId = R.drawable.italic_info_icon,
                ownText = "¿Cómo funciona\nesto?",
                iconSize = spacing.bigIconSize,
            )

            Spacer(Modifier.weight(1f))
        }


        Spacer(Modifier.weight(3f))

        NavigationBar(
            Modifier
                .fillMaxWidth()
                .height(spacing.barHeight),

            moreHighlighted = true,

            onHomeClicked = { viewModel.onHomeClicked() },
            onYouClicked = { viewModel.onYouClicked() },

            shadowOn = false,
        )
    }
}

@Composable
fun RectangleButton(
    modifier: Modifier = Modifier,

    ownImageId: Int,
    ownText: String,
    iconSize: Dp = YummiziTheme.spacing.hugeIconSize,

    onClick: () -> Unit = {},
){
    val spacing = YummiziTheme.spacing

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(spacing.mediumCornerRadius))
            .border(
                width = spacing.smallLineThickness,
                color = YummiziTheme.colors.yummiziGray,
                shape = RoundedCornerShape(spacing.mediumCornerRadius)
            )
            .background( YummiziTheme.colors.yummiziWhite)
            .padding(spacing.regularPadding)
            .clickable{
                onClick()
            }
    ){
        Image(
            painter = painterResource(ownImageId),
            colorFilter = ColorFilter.tint(color = YummiziTheme.colors.yummiziDarkBlue),
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
        )

        Spacer(Modifier.height(spacing.hugeSeparation))

        Text(
            text = ownText,
            color = YummiziTheme.colors.yummiziDarkBlue2,
            style = YummiziTheme.typography.teacherFontVar1,
        )
    }

}