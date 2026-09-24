package com.yummizi.app.features.restaurant.ui.preview.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.yummizi.app.core.designsystem.theme.AsyncYummiziImage
import com.yummizi.app.core.designsystem.theme.YummiziTheme
import kotlin.math.absoluteValue
import androidx.compose.ui.util.lerp

@Composable
fun MenuViewer( //TODO: Repair here, allow bellow shadow in each menu //TODO: Allow heighter menus
    modifier: Modifier = Modifier,

    images: List<String>,
    pagerState: PagerState
) {
    val spacing = YummiziTheme.spacing
    HorizontalPager(
        state = pagerState,
        // "contentPadding" creates the "Preview" effect (showing side edges)
        contentPadding = PaddingValues(horizontal = spacing.mediumPadding),   // Adjust this value to show more/less of sides
        pageSpacing = (-spacing.hugeSeparation*1.25f),
        modifier = modifier
            .height(480.dp) // Fixed height for consistency //TODO: Find something better than hardcode
    ) { page ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                // High Performance: GPU-accelerated scaling
                .graphicsLayer {
                    // Calculate how far this page is from the "snapped" center
                    val pageOffset = (
                            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                            ).absoluteValue

                    // Interpolate scale:
                    // Center item = 1.0 (100% size)
                    // Side items = 0.85 (85% size)
                    val scale = lerp(
                        start = 0.85f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )

                    scaleX = scale
                    scaleY = scale

                    // Slight fade for side items to emphasize center focus
                    alpha = lerp(
                        start = 0.6f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                }
                .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                .background(YummiziTheme.colors.yummiziWhite)
        ) {
            AsyncYummiziImage(
                model = images[page],
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}