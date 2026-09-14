package com.example.yummiziapp.features.auth.ui.emailmethod

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.yummiziapp.R
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
//import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yummiziapp.core.ui.components.YummiziDirectTextField
import com.example.yummiziapp.features.auth.ui.emailmethod.components.TermsModal
import com.example.yummiziapp.features.auth.ui.model.UserInfoTab

//@Preview()
@Composable
fun EmailMethodScreen(
    modifier: Modifier = Modifier,

    onHomeNavigation: () -> Unit,
    onIntroNavigation: () -> Unit,

    viewModel: EmailMethodViewModel = hiltViewModel()
){

    LaunchedEffect(Unit) {
        viewModel.navEffect.collect { effect ->
            when (effect) {
                is EmailMethodUiEvent.NavigateToHome -> { onHomeNavigation() }
                is EmailMethodUiEvent.NavigateToIntro -> { onIntroNavigation() }
            }
        }
    }

    val spacing = YummiziTheme.spacing

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var isReadingTerms by rememberSaveable { mutableStateOf(false) }

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
                .imePadding() // 🚀 Pushes content up when keyboard appears
                .verticalScroll(rememberScrollState()) // 🚀 Allows scrolling if content is too tall
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

            when(val state = uiState){
                is EmailMethodUiState.Loading -> {
                    // TODO
                }
                is EmailMethodUiState.InForm -> {
                    Text(
                        text = if(state.isSigningIn) "¡Iniciando sesión!" else "¡Creando cuenta!",
                        color = YummiziTheme.colors.yummiziWhite,
                        textAlign = TextAlign.Center,
                        style = YummiziTheme.typography.leaguespartanFontVar6
                    )
                }
                is EmailMethodUiState.Error -> {
                    // TODO
                }
            }


            Spacer(Modifier.height(spacing.hugeSeparation))

            val roundedCorner = RoundedCornerShape(spacing.mediumCornerRadius)

            Column(    // Auth methods container
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                    .background(color = YummiziTheme.colors.yummiziWhite)
                    .padding(spacing.regularPadding)
            ){

                when(val state = uiState){
                    is EmailMethodUiState.Loading -> {
                        Text(
                            text = "Cargando...",
                            color = YummiziTheme.colors.yummiziBlack,
                            style = YummiziTheme.typography.teacherFontVar2
                        )
                    }

                    is EmailMethodUiState.InForm -> {
                        if(!state.isSigningIn){   // TODO: Include the phone number
                            Text(
                                text = "Nombre de usuario:",
                                color = YummiziTheme.colors.yummiziBlack,
                                style = YummiziTheme.typography.teacherFontVar2
                            )

                            Spacer(Modifier.height(spacing.smallSeparation))

                            YummiziDirectTextField( // TODO: Figure out how to jump between inputs with the "Enter" keyboard
                                modifier = Modifier
                                    .fillMaxWidth(),

                                hintText = "e.g Pablo Perez",
                                backgroundColor = YummiziTheme.colors.yummiziGray3,
                                textStyle = YummiziTheme.typography.comfortaaFontVar4,

                                value = state.formState.username,
                                onValueChange = { viewModel.onFormTextModified(it, UserInfoTab.USERNAME) }
                            )

                            Spacer(Modifier.height(spacing.regularSeparation))
                        }


                        Text(
                            text = "Correo:",
                            color = YummiziTheme.colors.yummiziBlack,
                            style = YummiziTheme.typography.teacherFontVar2
                        )

                        Spacer(Modifier.height(spacing.smallSeparation))

                        YummiziDirectTextField(
                            modifier = Modifier
                                .fillMaxWidth(),

                            hintText = "e.g pablin@dominio.com",
                            backgroundColor = YummiziTheme.colors.yummiziGray3,
                            textStyle = YummiziTheme.typography.comfortaaFontVar4,

                            value = state.formState.email,
                            onValueChange = {
                                viewModel.onFormTextModified(it, UserInfoTab.EMAIL)
                            }
                        )

                        Spacer(Modifier.height(spacing.regularSeparation))

                        Text(
                            text = "Contraseña:",
                            color = YummiziTheme.colors.yummiziBlack,
                            style = YummiziTheme.typography.teacherFontVar2
                        )

                        Spacer(Modifier.height(spacing.smallSeparation))

                        YummiziDirectTextField(
                            modifier = Modifier
                                .fillMaxWidth(),

                            hintText = "e.g MakisLover321",
                            backgroundColor = YummiziTheme.colors.yummiziGray3,
                            textStyle = YummiziTheme.typography.comfortaaFontVar4,

                            value = state.formState.password,
                            onValueChange = {
                                viewModel.onFormTextModified(it, UserInfoTab.PASSWORD)
                            }
                        )

                        Spacer(Modifier.height(spacing.regularSeparation))

                        if(!state.isSigningIn){
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Box(
                                    modifier = Modifier
                                        .size(spacing.smallIconSize)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(spacing.tinyCornerRadius))
                                        .background(if (state.formState.acceptTerms) YummiziTheme.colors.yummiziLightOrange else YummiziTheme.colors.yummiziGray)
                                        .clickable{ viewModel.onTermsCheckBoxClicked() },

                                    )

                                Spacer(Modifier.width(spacing.mediumSeparation))

                                Text(
                                    text = "Acepto los ",
                                    color = YummiziTheme.colors.yummiziBlack,
                                    style = YummiziTheme.typography.teacherFontVar3
                                )

                                ///Linked text (orange and with underline)
                                Text(
                                    text = "Términos y Condiciones",
                                    color = YummiziTheme.colors.yummiziLightOrange,
                                    style = YummiziTheme.typography.teacherFontVar3,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier
                                        .clickable { isReadingTerms = true }
                                )
                            }

                            Spacer(Modifier.width(spacing.mediumSeparation))

                            Text(
                                text = "Al menos:\n+ 8 caracteres + 1 mayúscula\n+ 1 minúscula + 1 número\n+ 1 símbolo",
                                color = YummiziTheme.colors.yummiziDarkBlue,
                                style = YummiziTheme.typography.teacherFontVar3,
                                textAlign = TextAlign.Start
                            )
                        }

                        if(state.formState.formIssueText != null){
                            Spacer(Modifier.height(spacing.smallSeparation))

                            Text(
                                text = state.formState.formIssueText?: "",
                                color = YummiziTheme.colors.yummiziRed,
                                style = YummiziTheme.typography.teacherFontVar3
                            )
                        }

                        Spacer(Modifier.height(spacing.bigSeparation))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(spacing.sideLength),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Spacer(Modifier.weight(1f))

                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .clip(
                                        RoundedCornerShape(50)
                                    )
                                    .background(YummiziTheme.colors.yummiziLightOrange)
                                    .clickable {
                                        viewModel.onCreateAccountClicked()
                                    }

                            ){
                                Text(
                                    text = "Crear\ncuenta",
                                    textAlign = TextAlign.Center,
                                    color = YummiziTheme.colors.yummiziWhite,
                                    style = YummiziTheme.typography.teacherFontVar2,
                                    modifier = Modifier
                                        .padding(horizontal = spacing.bigPadding)
                                        .padding(vertical = spacing.mediumPadding)
                                        .align(Alignment.Center)
                                )
                            }

                            Spacer(Modifier.weight(1f))

                            if(state.isSigningIn){
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .clip(
                                            RoundedCornerShape(50)
                                        )
                                        .background(YummiziTheme.colors.yummiziLightOrange)
                                        .clickable {
                                            viewModel.onAccessAccountClicked()
                                        }

                                ){
                                    Text(
                                        text = "Acceder",
                                        textAlign = TextAlign.Center,
                                        color = YummiziTheme.colors.yummiziWhite,
                                        style = YummiziTheme.typography.teacherFontVar2,
                                        modifier = Modifier
                                            .padding(horizontal = spacing.bigPadding)
                                            .padding(vertical = spacing.mediumPadding)
                                            .align(Alignment.Center)
                                    )
                                }

                                Spacer(Modifier.weight(1f))
                            }

                        }

                    }

                    is EmailMethodUiState.Error -> {
                        // TODO
                    }

                }


            }
        }

        TermsModal(
            isOpen = isReadingTerms,
            onDismissRequest = { isReadingTerms = false }
        )
    }
}