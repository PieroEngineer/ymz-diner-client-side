package com.example.yummiziapp.features.auth.ui.authselection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yummiziapp.R
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
//import androidx.compose.ui.tooling.preview.Preview

//@Preview()
@Composable
fun AuthSelectionScreen(
    modifier: Modifier = Modifier,

    onEmailAuthClicked: () -> Unit,

    onHomeNavigation: () -> Unit,

    viewModel: AuthSelectionViewModel = hiltViewModel()
){

    val spacing = YummiziTheme.spacing

    LaunchedEffect(Unit) {
        viewModel.navEffect.collect { effect ->
            when (effect) {
                is AuthSelectionUiEvents.NavigateToEmailLogin -> { onEmailAuthClicked() }
                is AuthSelectionUiEvents.NavigateToHome -> { onHomeNavigation() }
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.auth_bg),
            contentDescription = null, // Backgrounds are decorative
            modifier = Modifier
                .blur(radius = spacing.mediumCornerRadius)
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(     // Main column
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = spacing.regularPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(R.drawable.yummizi_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(spacing.bigImageSize)
                    .clip(RoundedCornerShape(spacing.smallCornerRadius))
            )

            Spacer(Modifier.height(spacing.regularSeparation))

            Text(
                text = "¡Descubre la\nnueva experiencia!",
                color = YummiziTheme.colors.yummiziWhite,
                textAlign = TextAlign.Center,
                style = YummiziTheme.typography.leaguespartanFontVar6
            )

            Spacer(Modifier.height(spacing.hugeSeparation))

            val roundedCorner = RoundedCornerShape(spacing.mediumCornerRadius)

            Box(    // Auth methods container
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .border(
//                        width = spacing.mediumLineThickness,
//                        color = YummiziTheme.colors.yummiziWhite,
                        border = BorderStroke(spacing.bigLineThickness, YummiziTheme.colors.yummiziWhite),
                        shape = roundedCorner
                    )
                    .padding(spacing.regularPadding)
                    .background(
                        color = YummiziTheme.colors.yummiziWhite,
                        shape = roundedCorner
                    )
//                    .padding(spacing.mediumPadding)
            ){
                Image(
                    painter = painterResource(R.drawable.diffuse_bg),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(roundedCorner)
                        .alpha(0.5f),
                    contentScale = ContentScale.FillBounds
                )

                when (val state = uiState) {
                    is AuthSelectionUiState.Loading -> {
                        // Loading UI
                        CircularProgressIndicator(
                            modifier = Modifier.width(spacing.hugeIconSize),
                            color = YummiziTheme.colors.yummiziLightOrange,
                            trackColor = YummiziTheme.colors.yummiziWhite,
                        )
                    }
                    is AuthSelectionUiState.UnauthenticatedSession -> {
                        // Success UI
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Spacer(Modifier.weight(1f))

                            OrangeButtonWithIcon(
                                Modifier
                                    .weight(2f),
                                inlineText = "Correo",
                                iconId = R.drawable.mail_logo,
                                onClick = { viewModel.onEmailLoginClicked() }
                            )

                            Spacer(Modifier.weight(1f))

                            Box(
                                Modifier.weight(5f),
                                contentAlignment = Alignment.Center
                            ){
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ){
                                    OrangeButtonWithIcon(
                                        Modifier
                                            .weight(2f),
                                        inlineText = "Gmail",
                                        iconId = R.drawable.gmail_logo,
//                        onClick = { viewModel.onEmailAuthClicked() }
                                    )
                                    Spacer(Modifier.weight(1f))

                                    OrangeButtonWithIcon(
                                        Modifier
                                            .weight(2f),
                                        inlineText = "Facebook",
                                        iconId = R.drawable.facebook_logo,
//                        onClick = { viewModel.onEmailAuthClicked() }
                                    )
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
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun OrangeButtonWithIcon(
    modifier: Modifier = Modifier,

    inlineText: String,
    iconId: Int,

    onClick: () -> Unit = {},
){
    val spacing = YummiziTheme.spacing

    Row(    
        modifier = modifier
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ){
        Row(    // Orange frame
            modifier = Modifier
                .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                .background(YummiziTheme.colors.yummiziLightOrange)
//                .padding(vertical = spacing.regularCornerRadius)
                .padding(start = spacing.regularPadding)
        ){
            Text(
                text = inlineText,
                color = YummiziTheme.colors.yummiziWhite,
                style = YummiziTheme.typography.teacherFontVar4,
                modifier = Modifier
                    .padding(vertical = spacing.regularPadding)
                    .padding(end = spacing.enormousPadding)
            )
        }

        Box(    // Icon holder
            modifier = Modifier
                .fillMaxHeight()
                .offset(x = -(spacing.smallPadding + spacing.mediumLineThickness + spacing.bigIconSize/2))
                .border(
                    width = spacing.mediumLineThickness,
                    color = YummiziTheme.colors.yummiziDarkOrange,
                    shape = CircleShape
                )
                .background(
                    color = YummiziTheme.colors.yummiziWhite,
                    shape = CircleShape
                )
                .padding(spacing.regularPadding)
        ){
            Image(
                painter = painterResource(iconId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
//                    .size(spacing.bigIconSize)
            )
        }


    }
}