package com.example.yummiziapp.features.payment.ui.arrival.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme

@Composable
fun TextFieldForFeedback(
    modifier: Modifier = Modifier,

    value: String,
    onValueChange: (String) -> Unit,
) {
    val spacing = YummiziTheme.spacing

    val shape = RoundedCornerShape(spacing.mediumCornerRadius)

    // BasicTextField eliminates all default Material borders/indicators for full custom styling
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = spacing.mediumSeparation, shape = shape, clip = false)
            .background(color = Color.White, shape = shape),
        textStyle = YummiziTheme.typography.teacherFontVar3,
        cursorBrush = SolidColor(YummiziTheme.colors.yummiziBlack),
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.regularPadding, vertical = spacing.regularPadding),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = "Ejemplos:\n\nYo creo que yummizi fue ...\n\nLo que puede mejorar es ...\n\nMe gustó/disgustó que ...",
                        style = YummiziTheme.typography.teacherFontVar3
                    )
                }
                innerTextField()
            }
        }
    )
}