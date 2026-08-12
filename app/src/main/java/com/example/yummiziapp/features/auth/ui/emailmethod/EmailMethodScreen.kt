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
import com.example.yummiziapp.core.ui.components.YummiziTextField
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yummiziapp.features.auth.ui.emailmethod.components.TermsModal
import com.example.yummiziapp.features.auth.ui.model.UserInfoTab

@Preview()
@Composable
fun EmailMethodScreen(
    modifier: Modifier = Modifier,

    viewModel: EmailMethodViewModel = hiltViewModel()
){
    val spacing = YummiziTheme.spacing

    val isSigningIn by viewModel.isSigningIn.collectAsStateWithLifecycle()
    val formUiState by viewModel.formUiState.collectAsStateWithLifecycle()

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

            Text(
                text = if(isSigningIn) "¡Iniciando sesión!" else "¡Creando cuenta!",
                color = YummiziTheme.colors.yummiziWhite,
                textAlign = TextAlign.Center,
                style = YummiziTheme.typography.leaguespartanFontVar6
            )

            Spacer(Modifier.height(spacing.hugeSeparation))

            val roundedCorner = RoundedCornerShape(spacing.mediumCornerRadius)

            Column(    // Auth methods container
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                    .background(color = YummiziTheme.colors.yummiziWhite)
                    .padding(spacing.regularPadding)
            ){

                if(!isSigningIn){   // TODO: Include the phone number
                    Text(
                        text = "Nombre de usuario:",
                        color = YummiziTheme.colors.yummiziBlack,
                        style = YummiziTheme.typography.teacherFontVar2
                    )

                    Spacer(Modifier.height(spacing.smallSeparation))

                    YummiziTextField(
                        modifier = Modifier
                            .fillMaxWidth(),

                        hintText = "e.g Pablo Perez",
                        minLines = 1,
                        backgroundColor = YummiziTheme.colors.yummiziGray3,
                        textStyle = YummiziTheme.typography.comfortaaFontVar4,

                        value = formUiState.username,
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

                YummiziTextField(
                    modifier = Modifier
                        .fillMaxWidth(),

                    hintText = "e.g pabloperez@algúnCorreo.com",
                    minLines = 1,
                    backgroundColor = YummiziTheme.colors.yummiziGray3,
                    textStyle = YummiziTheme.typography.comfortaaFontVar4,

                    value = formUiState.email,
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

                YummiziTextField(
                    modifier = Modifier
                        .fillMaxWidth(),

                    hintText = "e.g MakisLover321",
                    minLines = 1,
                    backgroundColor = YummiziTheme.colors.yummiziGray3,
                    textStyle = YummiziTheme.typography.comfortaaFontVar4,

                    value = formUiState.password,
                    onValueChange = {
                        viewModel.onFormTextModified(it, UserInfoTab.PASSWORD)
                    }
                )

                Spacer(Modifier.height(spacing.regularSeparation))

                if(!isSigningIn){
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Box(
                            modifier = Modifier
                                .size(spacing.smallIconSize)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(spacing.tinyCornerRadius))
                                .background(if (formUiState.acceptTerms) YummiziTheme.colors.yummiziLightOrange else YummiziTheme.colors.yummiziGray)
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
                }

                if(formUiState.formIssueText != null){
                    Spacer(Modifier.height(spacing.smallSeparation))

                    Text(
                        text = formUiState.formIssueText?: "",
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

                    if(isSigningIn){
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
        }

        TermsModal(
            isOpen = isReadingTerms,
            onDismissRequest = { isReadingTerms = false }
        )
    }
}