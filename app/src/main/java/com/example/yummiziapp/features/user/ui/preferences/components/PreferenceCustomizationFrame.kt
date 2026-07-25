package com.example.yummiziapp.features.user.ui.preferences.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.R


@Composable
fun PreferenceCustomizationFrame(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit
){
    val spacing = YummiziTheme.spacing

    Column(
        modifier = modifier
            .dropShadow(
                shape = CircleShape,
                shadow = Shadow(
                    color = YummiziTheme.colors.yummiziBlack.copy(alpha = .5f),
                    radius = 25.dp,                          // High radius for a wide, soft spread
                    offset = DpOffset(x = 0.dp, y = 25.dp), // Offset only to the bottom
                )
            )
            .clip(RoundedCornerShape(spacing.mediumCornerRadius))
            .background(Color.White)
    ){

        Text(   // Section title
            text = "Personaliza tu preferencia",
            textAlign = TextAlign.Center,
            color = YummiziTheme.colors.yummiziLightOrange,
            style = YummiziTheme.typography.teacherFontVar5,
            modifier = Modifier
                .padding(horizontal = spacing.mediumPadding)
                .padding(top = spacing.mediumPadding)
                .align(Alignment.CenterHorizontally)

        )

        Spacer(Modifier.weight(1f))

        // Preference title text field
        //Xvar preferenceTitleText by remember { mutableStateOf("") }
        TextField(
            value = title,
            onValueChange = onTitleChange,
            // Align to the left of its Column parent
            modifier = Modifier
                .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                .align(Alignment.Start)
                .padding(horizontal = spacing.mediumPadding),

            // Define hint text (placeholder) and its style
            placeholder = {
                Text(   //TODO: Remove the padding between the hint (and likely text also) and the text field as such (In the other text field too)
                    text = "p. ej. Distribución de arroz",
                    style = YummiziTheme.typography.teacherFontVar4,
                    modifier = Modifier.alpha(.5f)
                )
            },

            // Define the style of the text written
            textStyle = YummiziTheme.typography.teacherFontVar4,

            // Modify background color (container color in Material 3)
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = YummiziTheme.colors.yummiziWhite,
                cursorColor = YummiziTheme.colors.yummiziDarkBlue,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )


        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(.4f)
                .padding(start = spacing.mediumPadding),
            thickness = spacing.smallLineThickness,
            color = YummiziTheme.colors.yummiziDarkBlue
        )

        // Preference content text field
        var preferenceContentText by remember { mutableStateOf("") }
        TextField(
            value = description,
            onValueChange = onDescriptionChange,
            // Align to the left of its Column parent
            modifier = Modifier
                .align(Alignment.Start)
                .padding(horizontal = spacing.mediumPadding),

            // Define hint text (placeholder) and its style
            placeholder = {
                // Make it transparent
                Text(
                    text = "p. ej. Me gusta mucho que haya poco arroz, pero bien esparcido por el plato (opcional)",
                    style = YummiziTheme.typography.teacherFontVar3,
                    modifier = Modifier.alpha(.5f)
                )
            },

            // Define the style of the text written
            textStyle = YummiziTheme.typography.teacherFontVar3,

            // Modify background color (container color in Material 3)
            colors = TextFieldDefaults.colors(
                focusedContainerColor = YummiziTheme.colors.yummiziLightOrange2,
                unfocusedContainerColor = YummiziTheme.colors.yummiziWhite,
                cursorColor = YummiziTheme.colors.yummiziDarkBlue,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(Modifier.weight(1.2f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.hugeIconSize) // It's because it defines the height of its images
                .padding(horizontal = spacing.mediumPadding)
                .padding(bottom = spacing.mediumPadding)
        ){

            Image(      // Preference icon
                painter = painterResource(R.drawable.preference_icon),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable {  }
                    .align(Alignment.CenterStart)
            )

            // Add button
            Image(      // Preference icon
                painter = painterResource(R.drawable.add_button),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable { onSave() }
                    .align(Alignment.Center)
            )

        }

    }
}