package com.example.yummiziapp.features.contact.ui.contact

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.yummiziapp.R
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yummiziapp.core.ui.components.NavigationBar
import com.example.yummiziapp.core.ui.components.FeedbackModal
import com.example.yummiziapp.features.contact.ui.contact.components.RestaurantContactModal
import com.example.yummiziapp.core.ui.components.SquareButton

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

    var wantToGiveFeedback by rememberSaveable { mutableStateOf(false) }
    var restaurantWantToContact by rememberSaveable { mutableStateOf(false) }

    val feedbackUiModel by viewModel.feedbackUiModel.collectAsStateWithLifecycle()  /// Is it a bad practice to have to create this state here? If yes, maybe it could go in the "combine" and use it, but the time condition would run every keystroke
    val restaurantContactUiModel by viewModel.restaurantContactUiModel.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
    ){
        Image(
            painter = painterResource(id = R.drawable.contact_bg),
            contentDescription = null, // Backgrounds are decorative
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

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

            Spacer(Modifier.weight(1f))


            Row(
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterHorizontally)
            ){
                SquareButton(
                    modifier = Modifier
                        .weight(1f),
                    ownImageId = R.drawable.conversation_icon,
                    ownText = "Tengo\ncomentarios",
                    onClick = { wantToGiveFeedback = true }
                )

                Spacer(Modifier.width(spacing.mediumSeparation))

                SquareButton(
                    modifier = Modifier
                        .weight(1f),
                    ownImageId = R.drawable.handshake_icon,
                    ownText = "Probar en mi\nrestaurante",
                    onClick = { restaurantWantToContact = true }
                )
            }

            Spacer(Modifier.height(spacing.mediumSeparation))

            Row(
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterHorizontally)
            ){
                Spacer(Modifier.weight(1f))

                SquareButton(
                    ownImageId = R.drawable.italic_info_icon,
                    ownText = "¿Cómo funciona\nesto?",
                    iconSize = spacing.bigIconSize,
                )

                Spacer(Modifier.weight(1f))
            }


            Spacer(Modifier.weight(1f))

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

        FeedbackModal(
            isOpen = wantToGiveFeedback,
            onDismissRequest = { wantToGiveFeedback = false },
            currentDescription = feedbackUiModel.description,
            currentConformity = feedbackUiModel.conformityDegree,
            onConformitySelected = {conformity -> viewModel.onConformityDegreeChanged(conformity) },
            onDescriptionChanged = { viewModel.onFeedbackDescriptionChanged(it) },
            onSend = {
                wantToGiveFeedback = false
                viewModel.onFeedbackReady()
                // TODO: Trigger a confirmation in a snackbar or in screen
            }
        )

        RestaurantContactModal( // TODO: Use the proper way to handle pop-ups and (#741)
            isOpen = restaurantWantToContact,
            onDismissRequest = { restaurantWantToContact = false},
            restaurantContactUiModel = restaurantContactUiModel,
            onTextFieldChanged = {restaurantInfo, newText -> viewModel.onRestaurantContactTextFieldChanged(restaurantInfo, newText) },
            onSend = {
                restaurantWantToContact = false
                viewModel.onRetaurantContactReady()
                // TODO: Trigger a confirmation in a snackbar or in screen
            },
        )
    }
}