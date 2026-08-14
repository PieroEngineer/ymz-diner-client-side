package com.example.yummiziapp.features.intro.ui.intro

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yummiziapp.R
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.core.ui.components.PageIndicator
import kotlin.math.roundToInt

@Composable
fun IntroScreen(
    modifier: Modifier = Modifier,

    viewModel: IntroViewModel = hiltViewModel(),

    onHomeNavigation: () -> Unit = {}
){

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(
        initialPage = uiState.activePageIndex,
        pageCount = { uiState.items.size }
    )

    val spacing = YummiziTheme.spacing


    // Sync page changes safely to ViewModel without interrupting gesture physics
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { pageIndex ->
            viewModel.onPageChanged(pageIndex)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .background(YummiziTheme.colors.yummiziWhite)
                .fillMaxSize()
//X            contentPadding = PaddingValues(horizontal = 32.dp),
//X            pageSpacing = 16.dp
        ) { pageIndex ->
            val carouselItem = uiState.items[pageIndex]

            Column(
                modifier = Modifier
                    .background(YummiziTheme.colors.yummiziWhite)
                    .fillMaxWidth()
                    .padding(top = spacing.regularPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!carouselItem.isLast){
                    Image(
                        painter = painterResource(R.drawable.yummizi_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(spacing.mediumImageSize)
                            .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                    )
                }

                if(!carouselItem.isLast){
                    Spacer(Modifier.height(spacing.regularPadding))
                }
                else{
                    Spacer(Modifier.weight(1f))
                }

                if(!carouselItem.isLast){
                    Image(
                        painter = painterResource(carouselItem.imageId),
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .weight(1f)
                            .layout { measurable, constraints ->
                                // 1. Measure the image normally based on its Column constraints
                                val placeable = measurable.measure(constraints)

                                // 2. Calculate the downward overlap (4.7% of its measured height)
                                val yOffset = if (carouselItem.imageId == R.drawable.double_thinking_in_cooking__image) {
                                    (placeable.height * 0.047f).roundToInt()
                                } else {
                                    0
                                }

                                // 3. Tell the Column that this item only consumes its natural height.
                                // This forces the next item in the Column to stay close,
                                // allowing our visual downward shift to draw right on top of it.
                                layout(placeable.width, placeable.height) {
                                    placeable.placeRelative(x = 0, y = yOffset)
                                }
                            }
                    )
                }
                else{
                    Image(
                        painter = painterResource(carouselItem.imageId),
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .size(spacing.bigImageSize)
                            .clip(RoundedCornerShape(spacing.mediumCornerRadius))
                    )
                }

                if(carouselItem.isLast) Spacer(Modifier.weight(1f))

                Column(
                    modifier = Modifier
                        .background(YummiziTheme.colors.yummiziDarkBlue)
                        .fillMaxWidth()
                        .height(spacing.bigBarHeight)
                        .padding(top = spacing.bigPadding, bottom = spacing.regularPadding)
                        .padding(horizontal = spacing.regularPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){

                    /// Description
                    Text(
                        text = carouselItem.phrase,
                        color = YummiziTheme.colors.yummiziWhite,
                        style = YummiziTheme.typography.leaguespartanFontVar6
                    )

                    Spacer(Modifier.weight(1f))

                    if(carouselItem.isLast){
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .clip(
                                    RoundedCornerShape(spacing.smallCornerRadius)
                                )
                                .background(YummiziTheme.colors.yummiziLightOrange)
                                .clickable {
                                    // TODO
                                }
                        ){
                            Text(
                                text = "Empezar",
                                textAlign = TextAlign.Center,
                                color = YummiziTheme.colors.yummiziWhite,
                                style = YummiziTheme.typography.teacherFontVar1,
                                modifier = Modifier
                                    .padding(horizontal = spacing.regularPadding)
                                    .padding(vertical = spacing.mediumPadding)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                    else{
                        Image(
                            painter = painterResource(R.drawable.sliding_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(spacing.bigIconSize)
                        )
                    }
                }
            }
        }

        PageIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = spacing.bigBarHeight*.9f),
            pageCount = uiState.items.size,
            pagerState = pagerState,

            activePointColor = YummiziTheme.colors.yummiziLightBlue,
            inactivePointColor = YummiziTheme.colors.yummiziWhite
        )
    }




}