package com.example.yummiziapp.features.user.ui.specification.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.yummiziapp.R
import com.example.yummiziapp.features.user.ui.model.UserDetailsUiModel


@Composable
fun CompanySection(
    modifier: Modifier = Modifier,
    details: UserDetailsUiModel,
    onCompanySizeUpdated: (Boolean) -> Unit,
    onUserIsAskingForModificationInCompanySize: () -> Unit
) {
    val spacing = YummiziTheme.spacing

    Box(
        modifier = modifier
    ){
        Box(    // Connection effect
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(spacing.bigCornerRadius)
                .fillMaxWidth()
                .background(YummiziTheme.colors.yummiziLightOrange2)
        )

        Column(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        bottomStart = spacing.mediumCornerRadius,
                        bottomEnd = spacing.bigCornerRadius
                    )
                )
                .background(YummiziTheme.colors.yummiziDarkOrange)
                .padding(horizontal = spacing.mediumPadding)
                .padding(vertical = spacing.smallPadding)
        ) {


            Text(    // Its header
                text = "${details.companySize+1} en mesa",
                color = YummiziTheme.colors.yummiziWhite,
                style = YummiziTheme.typography.leaguespartanFontVar2
            )

            Spacer(Modifier.height(spacing.mediumSeparation))

            Row(    // Current company joined   // TODO: It's going to change when friends are implemented in the app
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.mediumPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.weight(1f))

                DinerIcon()

                Spacer(Modifier.width(spacing.regularSeparation))

                if (details.companySize==0 && !details.companySizeIsFixed){
                    Image(
                        painter = painterResource(R.drawable.plus_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(spacing.bigIconSize)
                            .clickable { onCompanySizeUpdated(true) }
                    )
                }
                else{
                    Text(    // Its header
                        text = "y",
                        color = YummiziTheme.colors.yummiziWhite,
                        style = YummiziTheme.typography.leaguespartanFontVar2
                    )

                    Spacer(Modifier.width(spacing.regularSeparation))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        NumberSelector(
                            borderColor = YummiziTheme.colors.yummiziWhite,
                            rowColor = YummiziTheme.colors.yummiziWhite,

                            isFixed = details.companySizeIsFixed,

                            indicatorNumber = details.companySize,
                            onNumberChanged = { isIncreasing -> onCompanySizeUpdated(isIncreasing) },
                        )

                        Text(
                            text = "Acompañantes",
                            textAlign = TextAlign.Center,
                            color = YummiziTheme.colors.yummiziWhite,
                            style = YummiziTheme.typography.teacherFontVar1
                        )
                    }
                }

                Spacer(Modifier.width(spacing.regularSeparation))

                if (details.companySizeIsFixed){
                    Image(
                        painter = painterResource(R.drawable.info_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(spacing.mediumIconSize)
                            .clickable { onUserIsAskingForModificationInCompanySize() }
                    )
                }

                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun DinerIcon(
    modifier: Modifier = Modifier,
    userName: String = "Tú",    // TODO: this "Tú" is temporal until friends are implemented
//    companySize: Int = 0                                                   //
){
    val spacing = YummiziTheme.spacing
    Column(     // Diner icon
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
//        Box(){
            Image(
                painter = painterResource(R.drawable.user_icon),    /// Change it by user photo
                contentDescription = null,
                modifier = Modifier
                    .size(spacing.hugeIconSize)
            )

            //TODO: The extra friends go here later
//        }

        Spacer(Modifier.height(spacing.smallSeparation))
        Text(
            text = userName,
            color = YummiziTheme.colors.yummiziWhite,
            style = YummiziTheme.typography.teacherFontVar1
        )
    }
}

// TODO: Implement when different Yummizi's users can invite each other (Inside "Current company joined")
//                Box(  // Row with the Yummizi's users
//                    modifier = Modifier
//                        .weight(1f)
//                ){
//                    LazyRow(
//                        state = listState,
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.spacedBy(spacing.regularSeparation)
//                    ){
//                        items(
//                            items = currentCompanyInTable,
//                            key = { diner -> diner.id }
//                        ){
//                                diner ->
//
//                            Column(     // Diner icon
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ){
//                                Box(){
//                                    Image(
//                                        painter = painterResource(R.drawable.user_icon),    /// Change it by user photo
//                                        contentDescription = null,
//                                        modifier = Modifier
//                                            .size(spacing.hugeIconSize)
//                                    )
//
//                                    //TODO: The extra friends go here later
//                                }
//
//                                Spacer(Modifier.height(spacing.smallSeparation))
//
//                                Text(
//                                    text = diner.name,
//                                    color = YummiziTheme.colors.yummziziWhite,
//                                    style = YummiziTheme.typography.teacherFontVar1
//                                )
//                            }
//                        }
//                    }
//                }