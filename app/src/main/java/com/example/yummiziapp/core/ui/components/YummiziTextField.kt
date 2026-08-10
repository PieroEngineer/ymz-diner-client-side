package com.example.yummiziapp.core.ui.components

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
//import androidx.compose.ui.tooling.preview.Preview

//@Preview
@Composable
fun YummiziTextField(
    modifier: Modifier = Modifier,

    hintText: String,
    minLines: Int = 5,

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
        minLines = minLines, // Allows the field to be tall without forcing the cursor to the center
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.regularPadding, vertical = spacing.regularPadding),
                contentAlignment = Alignment.TopStart // Places both placeholder and cursor at the top-left
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = hintText,
                        style = YummiziTheme.typography.teacherFontVar3
                    )
                }
                innerTextField()
            }
        }
    )
}