package com.example.yummiziapp.features.user.ui.preferences.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.features.user.ui.model.PreferenceUiModel
import kotlin.uuid.Uuid
import com.example.yummiziapp.R

@Composable
fun PreferenceItem(
    modifier: Modifier = Modifier,
    preferenceUiModel: PreferenceUiModel,
    onDeletePreference: (Uuid) -> Unit
) {
    val spacing = YummiziTheme.spacing

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(spacing.smallCornerRadius))
            .background(YummiziTheme.colors.yummziziWhite),
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(      // Preference icon
            painter = painterResource(R.drawable.preference_icon),
            contentDescription = null,
            modifier = Modifier //TODO: adapt its color to the user selection
//                .fillMaxHeight()
                .padding(start = spacing.mediumPadding)
                .padding(vertical = spacing.mediumPadding)
                .weight(11.6f)
        )

        Spacer(Modifier.weight(4.4f))

        Column(
            modifier
                .padding(vertical = spacing.mediumPadding)
                .weight(66.4f)
        ) {
            Text(   // Preference title
                text = preferenceUiModel.title,
                style = YummiziTheme.typography.teacherFontVar4,
            )


            Text(   // Preference content
                text = preferenceUiModel.descriptionPart,
                style = YummiziTheme.typography.teacherFontVar3,
            )
        }

        Spacer(Modifier.weight(12f))

        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight(),
            thickness = spacing.bigLineThickness,
            color = YummiziTheme.colors.yummziziDarkBlue
        )

        Spacer(Modifier.weight(15.4f))

        // Edit button
        Image(      // Preference icon
            painter = painterResource(R.drawable.edit_button),  //TODO: While the preference is not uploaded, this pen icon is actually a loading icon that doesn't change until the preference is really uploaded
            contentDescription = null,
            modifier = Modifier //TODO: adapt its color to the user selection
                .padding(end = spacing.mediumPadding)
                .padding(vertical = spacing.mediumPadding)
                .size(spacing.bigIconSize)
                .clickable { onDeletePreference(preferenceUiModel.id) } //TODO: This is only a MockUp action. Implement a dialog window to edit the preference or delete it
        )
    }
}