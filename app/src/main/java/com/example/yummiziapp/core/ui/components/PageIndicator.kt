package com.example.yummiziapp.core.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme

@Composable
fun PageIndicator(
    modifier: Modifier = Modifier,
    pageCount: Int,
    pagerState: PagerState,

    activePointColor: Color = YummiziTheme.colors.yummiziDarkBlue,
    inactivePointColor: Color = YummiziTheme.colors.yummiziGray
) {
    val spacing = YummiziTheme.spacing
    Row(
        modifier = modifier
            //.height(spacing.regularCornerRadius)
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { iteration ->
            val isSelected = pagerState.currentPage == iteration

            // 3. Animation: Smoothly expand the width of the selected dot
            val width by animateDpAsState(
                targetValue = if (isSelected) spacing.regularIconSize else spacing.tinyIconSize,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "indicatorWidth"
            )

            // 4. Color Logic: Modify your colors here
            val color = if (isSelected) {
                activePointColor
            } else {
                inactivePointColor
            }

            Box(
                modifier = Modifier
                    .padding(spacing.smallPadding)
                    .height(spacing.mediumSeparation)
                    .width(width) // Applies the animated width
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}