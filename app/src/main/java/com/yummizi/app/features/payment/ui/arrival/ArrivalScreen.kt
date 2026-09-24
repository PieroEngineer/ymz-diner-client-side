package com.yummizi.app.features.payment.ui.arrival



//import androidx.compose.ui.tooling.preview.Preview
import android.content.ActivityNotFoundException
import android.content.Intent
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults.shape
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yummizi.app.R
import com.yummizi.app.core.designsystem.theme.AsyncYummiziImage
import com.yummizi.app.core.designsystem.theme.YummiziTheme
import com.yummizi.app.core.ui.components.ConformitySelector
import com.yummizi.app.core.ui.components.FeedbackModal
import com.yummizi.app.features.payment.ui.arrival.components.RemainingTimeText
import com.yummizi.app.core.ui.components.YummiziMultipleLineTextField
import com.yummizi.app.features.payment.ui.arrival.components.TimeProgressBar
import java.io.File
import androidx.core.net.toUri

//@Preview
@Composable
fun ArrivalScreen(  // TODO: For some reason, the keyboard is pushing the UI upwards and the complementary frame is cut sometimes (maybe for the weights)
    modifier: Modifier = Modifier,
    onHomeClickedNav: () -> Unit,
    viewModel: ArrivalViewModel = hiltViewModel()
) {
    val spacing = YummiziTheme.spacing

    val arrivalInfoUiState by viewModel.arrivalInfoUiState.collectAsStateWithLifecycle()

    var wantToGiveFeedback by rememberSaveable { mutableStateOf(false) }

    val feedbackUiState by viewModel.feedbackUiModel.collectAsStateWithLifecycle()  /// Is it a bad practice to have to create this state here? If yes, maybe it could go in the "combine" and use it, but the time condition would run every keystroke

    val snackbarHostState = remember { SnackbarHostState() }
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    // Listen for the ViewModel's commands
    LaunchedEffect(Unit) {
        viewModel.navEffect.collect { effect -> // TODO: Research the difference between "collectLatest" and "collect"
            when (effect) {
                is ArrivalUiEvent.NavigateToHome -> { onHomeClickedNav() }
                is ArrivalUiEvent.IntentNavigateToLink -> {
                    try {
                        uriHandler.openUri(effect.url)
                    } catch (e: Exception) {
                        // Fallback for specific deep links (e.g., WhatsApp intent scheme fallback)
                        val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                        try {
                            context.startActivity(intent)
                        } catch (anfe: ActivityNotFoundException) {
                            snackbarHostState.showSnackbar("No app found to open this link.")
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.diffuse_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.bigPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            when (val state = arrivalInfoUiState) {
                is ArrivalUiState.Loading -> {
                    // TODO: Put a block that contains a loading animation
                    Text(
                        text = "Loading data...",
                        textAlign = TextAlign.Center,
                        color = YummiziTheme.colors.yummiziDarkBlue,
                        style = YummiziTheme.typography.teacherFontVar3
                    )
                }
                is ArrivalUiState.CustomerArriving -> {
                    val context = LocalContext.current
                    val receiptFile = remember(state.arrivalInfo.paymentImagePath) {
                        File(context.filesDir, state.arrivalInfo.paymentImagePath)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ){
                        AsyncYummiziImage(    // Restaurant logo
                            model = state.arrivalInfo.restaurantLogoUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )

                        Box(
                            modifier = Modifier
                                .dropShadow( /// Apply the intense drop shadow
                                    shape = shape,
                                    shadow = Shadow(
                                        color = YummiziTheme.colors.yummiziBlack.copy(alpha = 0.2f),
                                        radius = 20.dp,                        // High radius for a wide, soft spread
                                    )
                                )
                                .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                                .padding(vertical = spacing.smallPadding)
                                .background(YummiziTheme.colors.yummiziWhite)
                                .padding(spacing.mediumPadding)
                                .clickable{ viewModel.onFindLocationClicked() }
                                .align(Alignment.CenterEnd)
                        ){
                            Image(
                                painter = painterResource(R.drawable.pin_icon),
                                colorFilter = ColorFilter.tint(color = YummiziTheme.colors.yummiziDarkBlue),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(spacing.hugeIconSize),
                            )
                        }
                    }

                    Column( // Main frame
                        modifier = Modifier
                            .weight(7f)
                            .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                            .background(YummiziTheme.colors.yummiziWhite)
                            .padding(
                                vertical = spacing.mediumPadding,
                                horizontal = spacing.bigPadding
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(   // Price
                            text = "S/. ${state.arrivalInfo.totalPayment}",
                            style = YummiziTheme.typography.leaguespartanFontVar4,
                            color = YummiziTheme.colors.yummiziBlack
                        )

                        Spacer(modifier = Modifier.height(spacing.smallSeparation))

                        HorizontalDivider(
                            thickness = spacing.smallLineThickness,
                            color = YummiziTheme.colors.yummiziGray
                        )

                        Spacer(modifier = Modifier.height(spacing.mediumSeparation))

                        Text(
                            text = "¡Pago enviado!",
                            textAlign = TextAlign.Center,
                            color = YummiziTheme.colors.yummiziDarkBlue,
                            style = YummiziTheme.typography.leaguespartanFontVar4,
                        )

                        Spacer(modifier = Modifier.height(spacing.mediumSeparation))

                        HorizontalDivider(
                            thickness = spacing.smallLineThickness,
                            color = YummiziTheme.colors.yummiziGray,
                            modifier = Modifier.fillMaxWidth(.3f)
                        )

                        Spacer(modifier = Modifier.height(spacing.mediumSeparation))

                        AsyncYummiziImage(
                            model = receiptFile,
                            contentDescription = null,
                            modifier = Modifier
//                                .size(spacing.smallImageSize)
                                .weight(2f)
                        )

                        Spacer(modifier = Modifier.height(spacing.mediumSeparation))

                        Text(
                            text = "Esperando tu llegada",
                            textAlign = TextAlign.Center,
                            color = YummiziTheme.colors.yummiziLightOrange,
                            style = YummiziTheme.typography.leaguespartanFontVar5,
                        )

                        RemainingTimeText(
                            targetInstant = state.arrivalInfo.waitEndsDatetime,
                            style = YummiziTheme.typography.leaguespartanFontVar4,
                            color = YummiziTheme.colors.yummiziLightOrange,
                            onTimeExpired = { viewModel.goToFeedbackState() }
                        )

                        Spacer(modifier = Modifier.height(spacing.mediumSeparation))

                        TimeProgressBar(
                            startInstant = state.arrivalInfo.waitBeginsDatetime,
                            endInstant = state.arrivalInfo.waitEndsDatetime,
                        )

                        Spacer(modifier = Modifier.height(spacing.smallSeparation))

                        HorizontalDivider(
                            thickness = spacing.smallLineThickness,
                            color = YummiziTheme.colors.yummiziGray
                        )

                        Spacer(modifier = Modifier.height(spacing.smallSeparation))

                        Text(
                            text = "¡ Gracias por ser parte de Yummizi !",
                            textAlign = TextAlign.Center,
                            style = YummiziTheme.typography.teacherFontVar6,
                            color = YummiziTheme.colors.yummiziDarkOrange,
                        )
                    }
                }
                is ArrivalUiState.CustomerArrived -> {

                    Image(
                        painter = painterResource(R.drawable.yummizi_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(spacing.mediumCornerRadius * .75f))
                    )

                    Column( // Main frame
                        modifier = Modifier
                            .weight(7f)
                            .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                            .background(YummiziTheme.colors.yummiziWhite)
                            .padding(
                                vertical = spacing.mediumPadding,
                                horizontal = spacing.bigPadding
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(   // Price
                            text = "¡ Gracias por\ntu visita\n${state.topMessagePart} !",
                            textAlign = TextAlign.Center,
                            style = YummiziTheme.typography.leaguespartanFontVar4,
                            color = YummiziTheme.colors.yummiziLightOrange
                        )

                        Spacer(modifier = Modifier.height(spacing.smallSeparation))

                        HorizontalDivider(
                            thickness = spacing.smallLineThickness,
                            color = YummiziTheme.colors.yummiziGray
                        )

                        Spacer(modifier = Modifier.height(spacing.mediumSeparation))

                        Text(
                            text = "Yummizi también se alimenta 🍲\n\nPor favor, compártenos tu experiencia con Yummizi",
                            textAlign = TextAlign.Center,
                            color = YummiziTheme.colors.yummiziDarkBlue,
                            style = YummiziTheme.typography.teacherFontVar3,
                        )

                        Spacer(modifier = Modifier.height(spacing.mediumSeparation))

                        YummiziMultipleLineTextField(
//                            modifier = Modifier.weight(1f),
                            value = feedbackUiState.description,
                            hintText = "Ejemplos:\n\nYo creo que yummizi fue ...\n\nLo que puede mejorar es ...\n\nMe gustó/disgustó que ...",
                            onValueChange = { viewModel.onDescriptionChanged(it) }
                        )

                        Spacer(modifier = Modifier.height(spacing.mediumSeparation))

                        ConformitySelector(
                            selectedConformity = feedbackUiState.conformityDegree,
                            onConformitySelected = {conformity -> viewModel.onConformityDegreeChanged(conformity) }
                        )

                        Spacer(modifier = Modifier.height(spacing.mediumSeparation))

                        Box(    // Payment confirmation button
                            modifier = Modifier
                                .clip(RoundedCornerShape(40)) // 40%
                                .background(YummiziTheme.colors.yummiziDarkBlue)
                                .padding(
                                    vertical = spacing.regularPadding,
                                    horizontal = spacing.bigPadding
                                )
                                .clickable{
                                    viewModel.onFeedbackReady()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Listo",
                                textAlign = TextAlign.Center,
                                style = YummiziTheme.typography.comfortaaFontVar3,
                                color = YummiziTheme.colors.yummiziWhite
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        HorizontalDivider(
                            thickness = spacing.smallLineThickness,
                            color = YummiziTheme.colors.yummiziGray
                        )

                        Spacer(modifier = Modifier.height(spacing.smallSeparation))

                        Text(
                            text = "Tu opinión es muy importante para nosotros",
                            style = YummiziTheme.typography.teacherFontVar6,
                            color = YummiziTheme.colors.yummiziDarkOrange,
                        )
                    }
                }
                is ArrivalUiState.Error -> {
                    // TODO: Put a block that contains a error message
                    Text(
                        text = "Error: Por favor, comunicarse con Yummizi y asista al restaurante (E#1#${state.message})",
                        textAlign = TextAlign.Center,
                        color = YummiziTheme.colors.yummiziDarkBlue,
                        style = YummiziTheme.typography.teacherFontVar3
                    )
                }
            }


            Spacer(modifier = Modifier.height(spacing.mediumSeparation))

            Column(    // Complementary frame
                modifier = Modifier
                    .weight(2f)
                    .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                    .background(YummiziTheme.colors.yummiziWhite)
                    .padding(spacing.mediumPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(   // Price
                    text = "¿Necesitas ayuda?",
                    style = YummiziTheme.typography.leaguespartanFontVar4,
                    color = YummiziTheme.colors.yummiziBlack
                )

                Spacer(modifier = Modifier.height(spacing.smallSeparation))

                HorizontalDivider(
                    thickness = spacing.smallLineThickness,
                    color = YummiziTheme.colors.yummiziGray
                )

                Spacer(modifier = Modifier.height(spacing.mediumSeparation))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Box(
                        modifier = Modifier
                            .dropShadow(shape = RoundedCornerShape(15)) {
                                alpha = 0.5f                                  // High opacity to make it visible
                                radius = 20f                                 // Soft blur radius
                                spread = 2f                               // Expands the shadow size
                                offset = Offset(8f, 12f) // Pushes shadow down
                            }
                            .clip(RoundedCornerShape(15)) // 50%
                            .background(YummiziTheme.colors.yummiziWhite)
                            .padding(
                                vertical = spacing.regularPadding,
                                horizontal = spacing.bigPadding
                            )
                            .clickable {
                                viewModel.onContactRestaurant()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Contactar\nrestaurante",
                            style = YummiziTheme.typography.comfortaaFontVar3,
                            color = YummiziTheme.colors.yummiziDarkBlue,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    if(arrivalInfoUiState !is ArrivalUiState.CustomerArrived){
                        Box(
                            modifier = Modifier
                                .dropShadow(shape = RoundedCornerShape(15)) {
                                    alpha = 0.5f                                  // High opacity to make it visible
                                    radius = 20f                                 // Soft blur radius
                                    spread = 2f                               // Expands the shadow size
                                    offset = Offset(8f, 12f) // Pushes shadow down
                                }
                                .clip(RoundedCornerShape(15)) // 50%
                                .background(YummiziTheme.colors.yummiziWhite)
                                .padding(
                                    vertical = spacing.regularPadding,
                                    horizontal = spacing.bigPadding
                                )
                                .clickable {
                                    wantToGiveFeedback = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Contactar\nYummizi",
                                style = YummiziTheme.typography.comfortaaFontVar3,
                                color = YummiziTheme.colors.yummiziDarkBlue,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        FeedbackModal(
            isOpen = wantToGiveFeedback,
            onDismissRequest = { wantToGiveFeedback = false },
            currentDescription = feedbackUiState.description,
            currentConformity = feedbackUiState.conformityDegree,
            onConformitySelected = {conformity -> viewModel.onConformityDegreeChanged(conformity) },
            onDescriptionChanged = { viewModel.onDescriptionChanged(it) },
            onSend = {
                wantToGiveFeedback = false
                viewModel.onFeedbackReady()
                // TODO: Trigger a confirmation in a snackbar or in screen
            }
        )
    }
}
