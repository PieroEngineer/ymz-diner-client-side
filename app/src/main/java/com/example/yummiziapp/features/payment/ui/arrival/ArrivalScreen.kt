package com.example.yummiziapp.features.payment.ui.arrival



//import androidx.compose.ui.tooling.preview.Preview
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yummiziapp.R
import com.example.yummiziapp.core.designsystem.theme.AsyncYummiziImage
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.features.payment.ui.arrival.components.ConformitySelector
import com.example.yummiziapp.features.payment.ui.arrival.components.RemainingTimeText
import com.example.yummiziapp.features.payment.ui.arrival.components.TextFieldForFeedback
import com.example.yummiziapp.features.payment.ui.arrival.components.TimeProgressBar
import com.example.yummiziapp.features.user.ui.specification.SpecificationUiEvents
import java.io.File

//@Preview
@Composable
fun ArrivalScreen(
    modifier: Modifier = Modifier,
    onHomeClickedNav: () -> Unit,
    viewModel: ArrivalViewModel = hiltViewModel()
) {
    val spacing = YummiziTheme.spacing

    val arrivalInfoUiState by viewModel.arrivalInfoUiState.collectAsStateWithLifecycle()

    // Listen for the ViewModel's commands
    LaunchedEffect(Unit) {
        viewModel.navEffect.collect { effect ->
            when (effect) {
                is ArrivalUiEvent.NavigateToHome -> { onHomeClickedNav() }
                else -> {}
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.payment_bg),
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

                    AsyncYummiziImage(    // Restaurant logo
                        model = state.arrivalInfo.restaurantLogoUrl,
                        contentDescription = null,
                        modifier = Modifier.weight(1f)
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
                            modifier = Modifier//.size(spacing.bigImageSize)
                                .weight(1f)
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
                            onTimeExpired = { event -> viewModel.goToFeedbackState(event) }
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
                    val feedbackUiState by viewModel.feedbackUiState.collectAsStateWithLifecycle()  /// Is it a bad practice to have to create this state here? If yes, maybe it could go in the "combine" and use it, but the time condition would run every keystroke

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

                        TextFieldForFeedback(
                            modifier = Modifier.weight(1f),
                            value = feedbackUiState.description,
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

                        Spacer(modifier = Modifier.height(spacing.smallSeparation))

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
                    .padding(spacing.bigPadding),
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
                    Box(    // Payment confirmation button
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
                                // TODO
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

                    Box(    // Payment confirmation button
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
                                // TODO
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
}
