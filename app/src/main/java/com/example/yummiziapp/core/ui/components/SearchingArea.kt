package com.example.yummiziapp.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.R
import androidx.compose.material3.TextFieldDefaults.shape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun SearchingArea(
    modifier: Modifier = Modifier
){
    val spacing = YummiziTheme.spacing
    val aiMicroBtn = painterResource(R.drawable.ia_micro_symbol)
    val searchIcon = painterResource(R.drawable.icon_search)

    val comfortaa = FontFamily(Font(R.font.comfortaa))

    Row(
        modifier = modifier.fillMaxWidth()
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ){
            Box(
                modifier = Modifier
                    .dropShadow( // Apply the intense drop shadow
                        shape = CircleShape,
                        //shape = shape,
                        shadow = Shadow(
                            color = YummiziTheme.colors.yummiziBlack.copy(alpha = .155f),
                            radius = 10.dp,                          // High radius for a wide, soft spread
                            offset = DpOffset(x = 0.dp, y = 15.dp), // Offset only to the bottom
                        )
                    )
                    .height(spacing.barHeight)
            ){
                Image(
                    painter = aiMicroBtn,
                    contentDescription = null, // Set to null for decorative backgrounds
                )
            }

            Box(
                modifier = Modifier
                    .dropShadow( // Apply the intense drop shadow
                        shape = shape,
                        shadow = Shadow(
                            color = YummiziTheme.colors.yummiziBlack.copy(alpha = 0.5f),
                            radius = 10.dp,                        // High radius for a wide, soft spread
                            offset = DpOffset(x = 4.dp, y = 8.dp) // Offset only to the bottom
                        )
                    )
            ) {
                var searchedText by remember { mutableStateOf("") }
                TextField(
                    value = searchedText,
                    onValueChange = { searchedText = it },
                    placeholder = {
                        Text(
                            "Explora...",
                            color = Color.Gray.copy(alpha = 0.6f),
                            fontFamily = comfortaa  //TODO: Standarized this with the theme
                        )
                    },
                    textStyle = TextStyle(
                        fontFamily = comfortaa,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.barHeight)
                        .clip(RoundedCornerShape(spacing.mediumCornerRadius * .75f)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = YummiziTheme.colors.yummiziDarkBlue,
                        unfocusedContainerColor = YummiziTheme.colors.yummiziDarkBlue,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    ),
                    trailingIcon = {
                        Icon(
                            painter = searchIcon,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = spacing.regularPadding)
                                .fillMaxHeight(.5f),
                            tint = Color.Unspecified
                        )
                    }
                )
            }

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(YummiziTheme.colors.yummiziBlack.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Próximamente",
                    color = YummiziTheme.colors.yummiziWhite,
                    style = YummiziTheme.typography.leaguespartanFontVar2
                )
            }
        }
    }
}