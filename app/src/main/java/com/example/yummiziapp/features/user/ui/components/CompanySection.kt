package com.example.yummiziapp.features.user.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.painterResource
import com.example.yummiziapp.R


@Composable
fun CompanySection(
    modifier: Modifier = Modifier,

    currentCompanyInTable: List<Diner>
) {
    val spacing = YummiziTheme.spacing
    val listState = rememberLazyListState()

    Box(
        modifier = modifier
    ){
        Box(    // Connection effect
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(spacing.bigCornerRadius)
                .fillMaxWidth()
                .background(YummiziTheme.colors.yummziziLightOrange2)
        )

        Column(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        bottomStart = spacing.mediumCornerRadius,
                        bottomEnd = spacing.bigCornerRadius
                    )
                )
                .background(YummiziTheme.colors.yummziziDarkOrange)
                .padding(horizontal = spacing.mediumPadding)
                .padding(vertical = spacing.smallPadding)
        ) {


            Text(    // Its header
                text = "${currentCompanyInTable.size+1} en mesa",
                color = YummiziTheme.colors.yummziziWhite,
                style = YummiziTheme.typography.leaguespartanFontVar2
            )

            Spacer(Modifier.height(spacing.mediumSeparation))

            Row(    // Current company joined
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.mediumPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                ){
                    LazyRow(
                        state = listState,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.regularSeparation)
                    ){
                        items(
                            items = currentCompanyInTable,
                            key = { diner -> diner.id }
                        ){
                                diner ->

                            Column(     // Diner icon
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Box(){
                                    Image(
                                        painter = painterResource(R.drawable.user_icon),    /// Change it by user photo
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(spacing.hugeIconSize)
                                    )

                                    //TODO: The extra friends go here later
                                }

                                Spacer(Modifier.height(spacing.smallSeparation))

                                Text(
                                    text = diner.name,
                                    color = YummiziTheme.colors.yummziziWhite,
                                    style = YummiziTheme.typography.teacherFontVar1
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.width(spacing.regularSeparation))

                Image(
                    painter = painterResource(R.drawable.plus_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(spacing.bigIconSize)
                )

            }
        }
    }
}

data class Diner(   // TODO: Temporal mock-up data class, it will be user
    val id: Int,
    val name: String
    ///val friends: List<Int>,  /// Here is the id of the friends
    //val currentFriendInTable: List<Int>
)