package com.example.yummiziapp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme

@Composable
fun YummiziDirectTextField(
    modifier: Modifier = Modifier,

    hintText: String,
    backgroundColor: Color = YummiziTheme.colors.yummiziWhite,
    textStyle: TextStyle = YummiziTheme.typography.teacherFontVar3,

    value: String,
    onValueChange: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    val spacing = YummiziTheme.spacing

    val shape = RoundedCornerShape(spacing.mediumCornerRadius)

    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = spacing.mediumSeparation, shape = shape, clip = false)
            .background(color = backgroundColor, shape = shape),

        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        cursorBrush = SolidColor(YummiziTheme.colors.yummiziBlack),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
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