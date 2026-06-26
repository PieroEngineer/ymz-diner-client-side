package com.example.yummiziapp.features.restaurant.ui.food.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.features.restaurant.ui.model.MenuUiModel
import kotlin.uuid.Uuid
import com.example.yummiziapp.R
import com.example.yummiziapp.features.restaurant.ui.components.PageIndicator

@Composable
fun MenuArea(
    modifier: Modifier = Modifier,

    currentPages: List<MenuUiModel>,
    onUpdatingSelectionQuantityClicked: (dishId: Uuid, delta: Int) -> Unit
){
    val spacing = YummiziTheme.spacing

    val nMenuPages = currentPages.size

    val pagerState = rememberPagerState(pageCount = { nMenuPages })


    Box(
        modifier = modifier
    ){

        HorizontalPager(    //TODO: convert it into a function where a boolean define if it's total or no total width, so it will be usable for the other menu composable
            state = pagerState,
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) { page ->

            Log.d("MyActivity", "🔎📇  Current menu is ${currentPages[page].page.id} of the restaurant ${currentPages[page].page.restaurantId}")

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                InteractiveMenu(
                    menuUiModel = currentPages[page],
                    onUpdatingSelectionQuantityClicked = onUpdatingSelectionQuantityClicked
                )
            }

        }

        Image(
            painter = painterResource(R.drawable.expand_button),
            contentDescription = null,
            modifier = Modifier
                .padding(spacing.bigPadding)
                .align(Alignment.TopEnd)
                .height(spacing.bigIconSize)

        )

        PageIndicator(
            modifier = Modifier.align(Alignment.BottomCenter),
            pageCount = nMenuPages,
            pagerState = pagerState
        )

    }
}