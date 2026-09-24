package com.yummizi.app.features.user.ui.preferences.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.yummizi.app.core.designsystem.theme.YummiziTheme
import com.yummizi.app.features.user.ui.model.PreferenceUiModel
import kotlin.uuid.Uuid

@Composable
fun PreferenceList(
    modifier: Modifier = Modifier,
    currentPreferences : List<PreferenceUiModel>,
    onDeletePreference: (Uuid) -> Unit
){
    val spacing = YummiziTheme.spacing
    val listState = rememberLazyListState()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally

    ){
        Box(    // Section title
            modifier = Modifier
                .dropShadow(
                    shape = CircleShape,
                    //shape = shape,
                    shadow = Shadow(
                        color = YummiziTheme.colors.yummiziBlack.copy(alpha = .5f),
                        radius = 25.dp,                          // High radius for a wide, soft spread
                        offset = DpOffset(x = 0.dp, y = 25.dp), // Offset only to the bottom
                    )
                )
                .fillMaxWidth()
                .clip(RoundedCornerShape(spacing.smallCornerRadius))
                .background(Color.White)
                .padding(horizontal = spacing.mediumPadding),
        ){

            Text(
                text = "Tus preferencias",
                textAlign = TextAlign.Center,
                color = YummiziTheme.colors.yummiziLightOrange,
                style = YummiziTheme.typography.teacherFontVar5,
                modifier = Modifier
                    .padding(vertical = spacing.mediumPadding)
                    .align(Alignment.Center)
            )
        }




        if (currentPreferences.isNotEmpty()) {

            Spacer(Modifier.height(spacing.mediumSeparation))

            LaunchedEffect(currentPreferences.size) {
                // Scroll to the very first item (index 0)
                listState.animateScrollToItem(0)
            }

            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(spacing.mediumSeparation),
                modifier = Modifier
                    .fillMaxWidth()
            ){
                items(
                    items = currentPreferences,
                    key = { uiItem -> uiItem.id }
                ){ uiItem ->

                    PreferenceItem(
                        Modifier
                            .fillMaxWidth(),
                        uiItem,
                        onDeletePreference
                    )
                }

                item {
                    Spacer(Modifier.height(spacing.barHeight))
                }
            }
        }
        else {
            Spacer(Modifier.weight(1f))
            Text(
                text = "Aún no ha\n\n" +
                        "incluido alguna\n\n" +
                        "preferencia\n\n" +
                        "personalizada...",
                textAlign = TextAlign.Center,
                color = YummiziTheme.colors.yummiziWhite,
                style = YummiziTheme.typography.comfortaaFontVar6,
            )
            Spacer(Modifier.weight(1f))
        }

    }
}