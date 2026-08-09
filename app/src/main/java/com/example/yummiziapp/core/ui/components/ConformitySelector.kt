package com.example.yummiziapp.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.yummiziapp.R
import com.example.yummiziapp.core.data.local.model.FeedbackLevelToken
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme

@Composable
fun ConformitySelector(
    modifier: Modifier = Modifier,
    selectedConformity: FeedbackLevelToken?,
    onConformitySelected: (FeedbackLevelToken) -> Unit
){
    val spacing = YummiziTheme.spacing

    Row(
        modifier = modifier
            .fillMaxWidth()
    ){
        Image(
            painter = painterResource(R.drawable.sad_face_icon),
            contentDescription = null,
            modifier = Modifier
                .size(spacing.regularIconSize)
                .clip(RoundedCornerShape(spacing.mediumCornerRadius * .75f))
                .clickable{ onConformitySelected(FeedbackLevelToken.BAD) },
            alpha = if (selectedConformity == FeedbackLevelToken.BAD) 1f else .5f
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(R.drawable.serious_face_icon),
            contentDescription = null,
            modifier = Modifier
                .size(spacing.regularIconSize)
                .clip(RoundedCornerShape(spacing.mediumCornerRadius * .75f))
                .clickable{ onConformitySelected(FeedbackLevelToken.NEUTRAL) },
            alpha = if (selectedConformity == FeedbackLevelToken.NEUTRAL) 1f else .5f
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(R.drawable.happy_face_icon),
            contentDescription = null,
            modifier = Modifier
                .size(spacing.regularIconSize)
                .clip(RoundedCornerShape(spacing.mediumCornerRadius * .75f))
                .clickable{ onConformitySelected(FeedbackLevelToken.GOOD) },
            alpha = if (selectedConformity == FeedbackLevelToken.GOOD) 1f else .5f
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(R.drawable.smiley_face_icon),
            contentDescription = null,
            modifier = Modifier
                .size(spacing.regularIconSize)
                .clip(RoundedCornerShape(spacing.mediumCornerRadius * .75f))
                .clickable{ onConformitySelected(FeedbackLevelToken.VERY_GOOD) },
            alpha = if (selectedConformity == FeedbackLevelToken.VERY_GOOD) 1f else .5f
        )

    }
}