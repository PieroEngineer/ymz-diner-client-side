package com.example.yummiziapp.features.payment.ui.beforepay

import android.util.Log
//import androidx.compose.ui.tooling.preview.Preview
//import com.example.yummiziapp.core.designsystem.theme.AsyncYummiziImage
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.yummiziapp.R
import com.example.yummiziapp.core.designsystem.theme.AsyncYummiziImage
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme

//@Preview
@Composable
fun PaymentPreparationScreen(
    modifier: Modifier = Modifier,
    viewModel: PaymentPreparationViewModel = hiltViewModel()
) {
    val spacing = YummiziTheme.spacing

    Log.w("MyActivity", "🔎🔎  1")

    val pickedImageUiState by viewModel.pickedImageUiState.collectAsState()

    Log.w("MyActivity", "🔎🔎  2")

    // System Photo Picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        viewModel.onImagePicked(uri)
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
//            AsyncYummiziImage(    // Restaurant logo
//                model = null, // Placeholder URL
//                contentDescription = null,
//                modifier = Modifier.weight(1f)
//            )
            Image(  //X Mockup
                painter = painterResource(id = R.drawable.mock_rest_logo_1),
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
                    text = "S/. 53,20", // Placeholder for decimal number
                    style = YummiziTheme.typography.leaguespartanFontVar4,
                    color = YummiziTheme.colors.yummiziBlack
                )

                Spacer(modifier = Modifier.height(spacing.smallSeparation))

                HorizontalDivider(
                    thickness = spacing.smallLineThickness,
                    color = YummiziTheme.colors.yummiziGray
                )

                Spacer(modifier = Modifier.height(spacing.mediumSeparation))

                Box(    // Payment methods' logos
                    modifier = Modifier
                        .fillMaxWidth(.5f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.yape_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(spacing.hugeIconSize)
                            .align(Alignment.CenterStart)
                            .fillMaxHeight()
                    )

                    Image(
                        painter = painterResource(id = R.drawable.plin_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(spacing.hugeIconSize)
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                    )
                }

                Spacer(modifier = Modifier.height(spacing.tinySeparation))

//                AsyncYummiziImage(    // Payment QR
//                    model = null, // Placeholder URL
//                    contentDescription = null,
//                    modifier = Modifier.size(spacing.bigImageSize)
//                )
                Image(  //X Mockup
                    painter = painterResource(id = R.drawable.mock_qr),
                    contentDescription = null,
                    modifier = Modifier.size(spacing.bigImageSize)
                )

                Spacer(modifier = Modifier.height(spacing.mediumSeparation))

                Text(
                    text = "O al número",
                    style = YummiziTheme.typography.comfortaaFontVar3,
                    color = YummiziTheme.colors.yummiziBlack,
                )

                Spacer(modifier = Modifier.height(spacing.smallSeparation))

                Row(    // Copiable phone number box
                    modifier = Modifier
//X                        .fillMaxWidth(.5f)
                        .weight(1f)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(spacing.smallCornerRadius))
                        .background(YummiziTheme.colors.yummiziWhite)
                        .padding(
                            vertical = spacing.tinyPadding,
                            horizontal = spacing.mediumPadding
                        ),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Text(
                        text = "987654321",
                        style = YummiziTheme.typography.comfortaaFontVar1,
                        color = YummiziTheme.colors.yummiziDarkBlue,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.width(spacing.mediumSeparation))

                    Image(
                        painter = painterResource(id = R.drawable.copy_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(spacing.smallIconSize)
                    )
                }

                Spacer(modifier = Modifier.height(spacing.smallSeparation))

                Text(
                    text = "Presionar el número para copiarlo",
                    style = YummiziTheme.typography.teacherFontVar6,
                    color = YummiziTheme.colors.yummiziGray
                )

                Spacer(modifier = Modifier.weight(.9f))

                Box(    // Payment confirmation button
                    modifier = Modifier
                        .clip(RoundedCornerShape(40)) // 40%
                        .background(YummiziTheme.colors.yummiziDarkBlue)
                        .padding(
                            vertical = spacing.regularPadding,
                            horizontal = spacing.bigPadding
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Confirmar pago",
                        style = YummiziTheme.typography.comfortaaFontVar3,
                        color = YummiziTheme.colors.yummiziWhite
                    )
                }

                Spacer(modifier = Modifier.weight(0.3f))

                HorizontalDivider(
                    thickness = spacing.smallLineThickness,
                    color = YummiziTheme.colors.yummiziGray
                )

                Spacer(modifier = Modifier.height(spacing.smallSeparation))

                Text(
                    text = "¡ Gracias por ser parte de Yummizi !",
                    style = YummiziTheme.typography.teacherFontVar6,
                    color = YummiziTheme.colors.yummiziDarkOrange,
                )
            }

            Spacer(modifier = Modifier.height(spacing.mediumSeparation))

            Row(    // Complementary frame
                modifier = Modifier
                    .weight(2f)
                    .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                    .background(YummiziTheme.colors.yummiziWhite)
                    .padding(spacing.bigPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (val state = pickedImageUiState) {
                    is PickedPaymentImageUiState.NoImageSelected -> {
                        Row(    // Upload image button
                            modifier = Modifier
                                .fillMaxSize()
                                .dropShadow(shape = RoundedCornerShape(12.dp)) {
                                    alpha =
                                        0.5f                                  // High opacity to make it visible
                                    radius = 20f                                 // Soft blur radius
                                    spread =
                                        2f                               // Expands the shadow size
                                    offset = Offset(8f, 12f) // Pushes shadow down
                                }
                                .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                                .background(YummiziTheme.colors.yummiziDarkBlue)
                                .padding(
                                    horizontal = spacing.mediumPadding,
                                    vertical = spacing.smallPadding
                                )
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = "Subir pago aquí",
                                style = YummiziTheme.typography.comfortaaFontVar4,
                                color = YummiziTheme.colors.yummiziWhite,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.width(spacing.regularSeparation))

                            Image(
                                painter = painterResource(id = R.drawable.picture_icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(spacing.bigIconSize)
                                    .fillMaxHeight()
                            )

                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    is PickedPaymentImageUiState.LoadingImageSelected -> {
                        Text(
                            text = "Cargando imagen",
                            style = YummiziTheme.typography.comfortaaFontVar4,
                            textAlign = TextAlign.Center
                        )
                    }

                    is PickedPaymentImageUiState.ImageAlreadySelected -> {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Payment image
                            AsyncYummiziImage(
                                model = state.selectedFile.absolutePath,
                                contentDescription = null,
                                modifier = Modifier.size(spacing.mediumImageSize)
                            )

                            // Vertical separator line
                            VerticalDivider(
                                thickness = spacing.smallLineThickness,
                                color = YummiziTheme.colors.yummiziGray
                            )

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "¡ Imagen subida !",
                                    style = YummiziTheme.typography.comfortaaFontVar1,
                                    color = YummiziTheme.colors.yummiziDarkBlue,
                                    textAlign = TextAlign.Center
                                )

                                // Change button
                                Box(    // Payment confirmation button
                                    modifier = Modifier
                                        .dropShadow(shape = RoundedCornerShape(50)) {
                                            alpha =
                                                0.5f                                  // High opacity to make it visible
                                            radius = 20f                                 // Soft blur radius
                                            spread =
                                                2f                               // Expands the shadow size
                                            offset = Offset(8f, 12f) // Pushes shadow down
                                        }
                                        .clip(RoundedCornerShape(50)) // 50%
                                        .background(YummiziTheme.colors.yummiziWhite)
                                        .padding(
                                            vertical = spacing.regularPadding,
                                            horizontal = spacing.bigPadding
                                        )
                                        .clickable {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Cambiar",
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
        }
    }
}
