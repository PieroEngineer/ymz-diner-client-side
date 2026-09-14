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
import androidx.compose.ui.text.TextStyle
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
//import androidx.compose.ui.tooling.preview.Preview

//@Preview
@Composable
fun YummiziMultipleLineTextField(   // TODO: Unified it with the other Yummizi's textfield
    modifier: Modifier = Modifier,

    hintText: String,
    minLines: Int = 5,
    backgroundColor: Color = YummiziTheme.colors.yummiziWhite,
    textStyle: TextStyle = YummiziTheme.typography.teacherFontVar3,

    value: String,
    onValueChange: (String) -> Unit,
) {
    val spacing = YummiziTheme.spacing

    val shape = RoundedCornerShape(spacing.mediumCornerRadius)

    // BasicTextField eliminates all default Material borders/indicators for full custom styling
    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = spacing.mediumSeparation, shape = shape, clip = false)
            .background(color = backgroundColor, shape = shape),

        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
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
                        style = textStyle
                    )
                }
                innerTextField()
            }
        }
    )
}