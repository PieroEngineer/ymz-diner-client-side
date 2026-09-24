package com.yummizi.app.features.home.ui.feed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yummizi.app.core.designsystem.theme.YummiziTheme
import androidx.compose.foundation.lazy.LazyListScope

@Composable
fun SectionArea(
    modifier: Modifier = Modifier,

    subDivisionContent: LazyListScope.() -> Unit

){
    val spacing = YummiziTheme.spacing

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.mediumSeparation)
    ) {
        subDivisionContent()
    }

}