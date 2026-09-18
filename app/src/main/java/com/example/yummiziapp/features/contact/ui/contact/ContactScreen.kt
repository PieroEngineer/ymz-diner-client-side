package com.example.yummiziapp.features.contact.ui.contact

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.util.Log
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.yummiziapp.R
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yummiziapp.core.ui.components.NavigationBar
import com.example.yummiziapp.core.ui.components.FeedbackModal
import com.example.yummiziapp.features.contact.ui.contact.components.ShareYummiziModal
import com.example.yummiziapp.core.ui.components.SquareButton
import com.example.yummiziapp.core.ui.components.YummiziSnackbar

//@Preview()
@Composable
fun ContactScreen(
    modifier: Modifier = Modifier,

    onHomeClickedNav: () -> Unit,
    onYouClickedNav: () -> Unit,

    viewModel: ContactViewModel = hiltViewModel()
){
    val spacing = YummiziTheme.spacing

    val snackbarHostState = remember { SnackbarHostState() }

    val clipboard = LocalClipboard.current

    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.navEffect.collect { effect ->
            when (effect) {
                is ContactUiEvent.NavigateToHome -> { onHomeClickedNav() }
                is ContactUiEvent.NavigateToPreferences -> { onYouClickedNav() }
                is ContactUiEvent.IntentNavigateToLink -> {
                    try {
                        uriHandler.openUri(effect.url)
                    } catch (e: Exception) {
                        // Fallback for specific deep links
                        val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                        try {
                            context.startActivity(intent)
                        } catch (anfe: ActivityNotFoundException) {
                            Log.d("MyActivity", "🚩  No se pudo abrir el link")
                        }
                    }
                }
                is ContactUiEvent.CopyToClipboard -> {
                    // Create Android standard ClipData
                    val clipData = ClipData.newPlainText("url", effect.copiedText)

                    // Call the modern suspend function to set the clip
                    clipboard.setClipEntry(ClipEntry(clipData))

                    snackbarHostState.showSnackbar(message = effect.accompanyingText)
                }
            }
        }
    }

    var wantToGiveFeedback by rememberSaveable { mutableStateOf(false) }
    var userWantToShare by rememberSaveable { mutableStateOf(false) }

    val feedbackUiModel by viewModel.feedbackUiModel.collectAsStateWithLifecycle()  /// Is it a bad practice to have to create this state here? If yes, maybe it could go in the "combine" and use it, but the time condition would run every keystroke
    val restaurantContactUiModel by viewModel.restaurantContactUiModel.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
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
                style = YummiziTheme.typography.leaguespartanFontVar7,
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

//            Spacer(Modifier.weight(1f))

            Spacer(Modifier.height(spacing.mediumSeparation))

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
                    ownText = "Compartir a\nusuario/restaurante",
                    onClick = { userWantToShare = true }
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
                    onClick = { viewModel.onTryToSeeVideo() }
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

        ShareYummiziModal( // TODO: Use the proper way to handle pop-ups and (#741)
            isOpen = userWantToShare,
            onDismissRequest = { userWantToShare = false},
//            restaurantContactUiModel = restaurantContactUiModel,
//            onTextFieldChanged = {restaurantInfo, newText -> viewModel.onRestaurantContactTextFieldChanged(restaurantInfo, newText) },
            onReady = {
                userWantToShare = false
//                viewModel.onRestaurantContactReady()
                // TODO: Trigger a confirmation in a snackbar or in screen
            },
            onCopyUrl = {
                viewModel.onCopyYummiziLink()
            }
        )

        YummiziSnackbar(
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, start = 32.dp, end = 32.dp)
        )
    }
}