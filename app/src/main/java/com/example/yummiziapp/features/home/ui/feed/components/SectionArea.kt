package com.example.yummiziapp.features.home.ui.feed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
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