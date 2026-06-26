package com.example.yummiziapp.features.home.ui.feed.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import androidx.compose.foundation.lazy.LazyListScope

@Composable
fun RestaurantScrollable(
    modifier: Modifier = Modifier,

    containerSectionId: Int?,
    reelActivated: Boolean,
    onScrollUsed: (Int?) -> Unit,

    subDivisionContent: LazyListScope.() -> Unit
) {
    val spacing = YummiziTheme.spacing

    val listState = rememberLazyListState()

    // Use derivedStateOf to observe the scrolling state efficiently
    val isBeingManipulated by remember {
        derivedStateOf { listState.isScrollInProgress }
    }

    LaunchedEffect(isBeingManipulated) {
        if (isBeingManipulated) {
            // When scroll starts, notify the parent.
            onScrollUsed(containerSectionId)
        } else if (reelActivated){
            // When scroll ends, notify the parent to clear the active state.
            onScrollUsed(containerSectionId)
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = spacing.regularPadding),
        horizontalArrangement = Arrangement.spacedBy(spacing.regularSeparation)
    ) {
        subDivisionContent()
    }

}