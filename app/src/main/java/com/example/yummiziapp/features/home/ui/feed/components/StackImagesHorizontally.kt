package com.example.yummiziapp.features.home.ui.feed.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.zIndex
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.core.designsystem.theme.YummiziImage

//@Preview
@Composable
fun StackImagesHorizontally(    ///IDEA: It would be nice add an effect here
    modifier: Modifier = Modifier,

    imagesToStack: List<String>
){
    val spacing = YummiziTheme.spacing
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement
            .spacedBy(-spacing.hugeSeparation)     //TODO: Make the horizontal separation dynamic. It should depend of the parent width
    ){
        imagesToStack.forEachIndexed{ index, imageUrl ->
            YummiziImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                    .zIndex((imagesToStack.size - index).toFloat()),
                alignment = Alignment.TopStart
            )

        }
    }
}