package com.example.yummiziapp.features.user.ui.preferences

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.R
import com.example.yummiziapp.core.ui.components.NavigationBar
import com.example.yummiziapp.features.user.ui.components.PreferenceCustomizationFrame
import com.example.yummiziapp.features.user.ui.components.PreferenceList


//@Preview(showBackground = true)
@Composable
fun PreferencesScreen(
    modifier: Modifier = Modifier,
    viewModel: PreferenceViewModel = hiltViewModel()
){

    val currentPreferences by viewModel.preferences.collectAsStateWithLifecycle()

    val spacing = YummiziTheme.spacing

    Box(
        modifier = modifier
    ){

        Image(
            painter = painterResource(id = R.drawable.preference_bg),
            contentDescription = null, // Backgrounds are decorative
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(     // Main column
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Row(    // Top elements
                modifier = Modifier
                    .weight(9f)
                    .padding(horizontal = spacing.smallPadding)
                    .padding(top = spacing.mediumPadding),
                verticalAlignment = Alignment.CenterVertically
            ){
                Spacer(Modifier.weight(1f))


                Box(    // Back button
                    modifier = Modifier
                        .background(Color.White, shape = CircleShape)
                        .size(spacing.hugeIconSize),
//                        .padding(vertical = spacing.smallPadding),
                    contentAlignment = Alignment.Center
                ){

                    Image(
                        painter = painterResource(R.drawable.row_btn),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = YummiziTheme.colors.yummziziBlack),
                        modifier = Modifier
//                            .padding(
//                                vertical = spacing.mediumPadding,
//                                horizontal = spacing.regularPadding
//                            )
                            .size(spacing.regularIconSize)
                            .rotate(270f)
                            .clickable {  }

                    )
                }

                Spacer(Modifier.weight(3f))


                Box(    // Screen title
                    modifier = Modifier
                        .dropShadow(
                            shape = CircleShape,
                            shadow = Shadow(
                                color = YummiziTheme.colors.yummziziBlack.copy(alpha = .5f),
                                radius = 25.dp,                          // High radius for a wide, soft spread
                                offset = DpOffset(x = 0.dp, y = 25.dp), // Offset only to the bottom
                            )
                        )
                        .clip(RoundedCornerShape(spacing.smallCornerRadius))
                        .background(Color.White),
//                        .padding(vertical = spacing.smallPadding),
                    contentAlignment = Alignment.Center

                ){

                    Text(   //TODO: Make it bigger
                        text = "Preferencias",
                        textAlign = TextAlign.Center,
                        color = YummiziTheme.colors.yummziziLightOrange,
                        style = YummiziTheme.typography.teacherFontVar7,
                        modifier = Modifier
//X                            .padding(start = spacing.smallPadding)
                            .padding(spacing.regularPadding)
                            .align(Alignment.Center)
                    )
                }

                Spacer(Modifier.weight(3f))

                Image(      // Info button
                    painter = painterResource(R.drawable.info_button),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = YummiziTheme.colors.yummziziWhite),
                    modifier = Modifier
                        .size(spacing.hugeIconSize)
                        .clickable {  }

                )

                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.weight(2f))

            PreferenceCustomizationFrame(
                Modifier
                    .padding(horizontal = spacing.regularPadding)
                    .weight(30f),
                title = viewModel.titleInput,
                description = viewModel.descInput,
                onTitleChange = { viewModel.titleInput = it },
                onDescriptionChange = { viewModel.descInput = it },
                onSave = { viewModel.onSaveClicked() }
            )

            Spacer(Modifier.weight(4f))

            PreferenceList(
                Modifier
                    .padding(horizontal = spacing.regularPadding)
                    .weight(48f),
                currentPreferences,
                onDeletePreference = { id -> viewModel.onDeletePreference(id) }
            )



        }


        NavigationBar(
            Modifier
                .fillMaxWidth()
                .height(spacing.barHeight)
                .align(Alignment.BottomCenter)
        )


    }
}