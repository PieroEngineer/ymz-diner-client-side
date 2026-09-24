package com.yummizi.app.features.auth.ui.emailmethod.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import com.yummizi.app.core.designsystem.theme.YummiziTheme

@Composable
fun TermsModal(
    modifier: Modifier = Modifier,

    isOpen: Boolean = false,
    onDismissRequest: () -> Unit = {}
){
    val spacing = YummiziTheme.spacing
    val scrollState = rememberScrollState()

    // 🛡️ Hardware Back Button Interception
    if (isOpen) {
        BackHandler { onDismissRequest() }
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // THE INTERCEPTION MASK (Locks screen inputs and darkens background)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(YummiziTheme.colors.yummiziBlack.copy(alpha = 0.5f)) // Transparent dark cover
                    .pointerInput(Unit) {
                        // 🚀 Swallow all touch configurations so background elements are completely frozen!
                    }
                    .clickable { onDismissRequest() }
            )

            // THE POP-UP FRAME
            Column(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* 🛡️ Consume taps to prevent "click-through" to the background mask */ }
                    .clip(
                        RoundedCornerShape(
                            topStart = spacing.mediumCornerRadius,
                            topEnd = spacing.bigCornerRadius,
                            bottomStart = spacing.bigCornerRadius,
                            bottomEnd = spacing.mediumCornerRadius
                        )
                    )
                    .border(
                        BorderStroke(spacing.mediumLineThickness, YummiziTheme.colors.yummiziDarkOrange),
                        shape = RoundedCornerShape(
                            topStart = spacing.mediumCornerRadius,
                            topEnd = spacing.bigCornerRadius,
                            bottomStart = spacing.bigCornerRadius,
                            bottomEnd = spacing.mediumCornerRadius
                        )
                    )
                    .background(YummiziTheme.colors.yummiziWhite)
                    .fillMaxWidth(0.85f)
                    .padding(spacing.bigPadding)
                    .verticalScroll(scrollState),
            ){

                HorizontalDivider(
                    thickness = spacing.mediumLineThickness,
                    color = YummiziTheme.colors.yummiziDarkOrange,
                    modifier = Modifier
                        .fillMaxWidth(.5f)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(spacing.regularSeparation))

                Text(
                    text = "Términos y condiciones",
                    color = YummiziTheme.colors.yummiziLightOrange,
                    style = YummiziTheme.typography.leaguespartanFontVar6
                )

                Spacer(Modifier.height(spacing.regularSeparation))

                Text(
                    text = "1. Identidad y Domicilio",
                    color = YummiziTheme.colors.yummiziDarkBlue,
                    style = YummiziTheme.typography.leaguespartanFontVar3
                )

                Spacer(Modifier.height(spacing.smallSeparation))

                Text(
                    text = "El responsable del tratamiento de sus datos personales es Piero Stephano Olivas Tuesta, en representación del proyecto Yummizi (en formación), con domicilio para estos efectos en Lima, Perú.",
                    color = YummiziTheme.colors.yummiziBlack,
                    style = YummiziTheme.typography.teacherFontVar3
                )

                Spacer(Modifier.height(spacing.regularSeparation))

                Text(
                    text = "2. Finalidad",
                    color = YummiziTheme.colors.yummiziDarkBlue,
                    style = YummiziTheme.typography.leaguespartanFontVar3
                )

                Spacer(Modifier.height(spacing.smallSeparation))

                Text(
                    text = "Los datos personales solicitados a través de nuestro formulario (Nombre, Correo Electrónico, Nombre del Restaurante y comentarios extra) serán tratados para las siguientes finalidades:\n" +
                            "\n" +
                            "- Registrar su interés en el lanzamiento de la plataforma Yummizi.\n" +
                            "\n" +
                            "- Registrar su perfil en la aplicación (En caso el formulario esté en la aplicación)\n" +
                            "\n" +
                            "- Enviarle actualizaciones y noticias relacionadas directamente con nuestro servicio.\n" +
                            "\n" +
                            "- Contactarlo para realizar encuestas de mercado o pruebas del producto.\n" +
                            "\n" +
                            "Adicionalmente, en caso usted esté ingresando desde la aplicativo, lo solicitado también es la contraseña que usted desee.",
                    color = YummiziTheme.colors.yummiziBlack,
                    style = YummiziTheme.typography.teacherFontVar3
                )

                Spacer(Modifier.height(spacing.regularSeparation))

                Text(
                    text = "3. Banco de Datos y Plazo de Conservación",
                    color = YummiziTheme.colors.yummiziDarkBlue,
                    style = YummiziTheme.typography.leaguespartanFontVar3
                )

                Spacer(Modifier.height(spacing.smallSeparation))

                Text(
                    text = "Sus datos serán almacenados en el banco de datos personales denominado \"Yummizi LP\", de titularidad de Yummizi. Conservaremos esta información por un periodo de 2 años o hasta que usted decida revocar su consentimiento.",
                    color = YummiziTheme.colors.yummiziBlack,
                    style = YummiziTheme.typography.teacherFontVar3
                )

                Spacer(Modifier.height(spacing.regularSeparation))

                Text(
                    text = "4. Transferencia y Flujo Transfronterizo",
                    color = YummiziTheme.colors.yummiziDarkBlue,
                    style = YummiziTheme.typography.leaguespartanFontVar3
                )

                Spacer(Modifier.height(spacing.smallSeparation))

                Text(
                    text = "Para el correcto funcionamiento de esta aplicación, utilizamos servicios de terceros (netlify, supabase y AWS), cuyos servidores se encuentran fuera del Perú (generalmente en EE.UU.). Esto constituye una transferencia internacional de datos, la cual se realiza bajo estándares de seguridad adecuados.",
                    color = YummiziTheme.colors.yummiziBlack,
                    style = YummiziTheme.typography.teacherFontVar3
                )

                Spacer(Modifier.height(spacing.regularSeparation))

                Text(
                    text = "5. Derechos ARCO",
                    color = YummiziTheme.colors.yummiziDarkBlue,
                    style = YummiziTheme.typography.leaguespartanFontVar3
                )

                Spacer(Modifier.height(spacing.smallSeparation))

                Text(
                    text = "Usted puede ejercer sus derechos de Acceso, Rectificación, Cancelación y Oposición (ARCO) en cualquier momento. Para ello, solo debe enviar una solicitud por correo electrónico a: yummizi.mkt@gmail.com con el asunto \"Derechos ARCO\".",
                    color = YummiziTheme.colors.yummiziBlack,
                    style = YummiziTheme.typography.teacherFontVar3
                )

                Spacer(Modifier.height(spacing.regularSeparation))

                Text(
                    text = "6. Obligatoriedad",
                    color = YummiziTheme.colors.yummiziDarkBlue,
                    style = YummiziTheme.typography.leaguespartanFontVar3
                )

                Spacer(Modifier.height(spacing.smallSeparation))

                Text(
                    text = "Para registrarse, es obligatorio completar los campos señalados. De lo contrario, no podremos contactarlo ni incluirlo en nuestra lista de espera para el lanzamiento o el registro de la aplicación.",
                    color = YummiziTheme.colors.yummiziBlack,
                    style = YummiziTheme.typography.teacherFontVar3
                )

                Spacer(Modifier.height(spacing.mediumSeparation))

                HorizontalDivider(
                    thickness = spacing.mediumLineThickness,
                    color = YummiziTheme.colors.yummiziDarkOrange,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(1f)
                )

                Spacer(Modifier.height(spacing.mediumSeparation))

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clip(
                            RoundedCornerShape(spacing.smallCornerRadius)
                        )
                        .background(YummiziTheme.colors.yummiziBlue)
                        .clickable {
                            onDismissRequest()
                        }
                ){
                    Text(
                        text = "Continuar",
                        textAlign = TextAlign.Center,
                        color = YummiziTheme.colors.yummiziWhite,
                        style = YummiziTheme.typography.teacherFontVar1,
                        modifier = Modifier
                            .padding(horizontal = spacing.regularPadding)
                            .padding(vertical = spacing.mediumPadding)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}