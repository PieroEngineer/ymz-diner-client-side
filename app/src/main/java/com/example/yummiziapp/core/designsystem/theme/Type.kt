package com.example.yummiziapp.core.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.yummiziapp.R

// Set of Material typography styles to start with

data class YummiziTypography (
    val teacherFontVar1: TextStyle,
    val teacherFontVar2: TextStyle,
    val teacherFontVar3: TextStyle,
    val teacherFontVar4: TextStyle,
    val teacherFontVar5: TextStyle,
    val teacherFontVar6: TextStyle,
    val teacherFontVar7: TextStyle,
    val teacherFontVar8: TextStyle,
    val teacherFontVar9: TextStyle,
    val comfortaaFontVar1: TextStyle,
    val comfortaaFontVar2: TextStyle,
    val comfortaaFontVar3: TextStyle,
    val comfortaaFontVar4: TextStyle,
    val comfortaaFontVar5: TextStyle,
    val comfortaaFontVar6: TextStyle,
    val leaguespartanFontVar1: TextStyle,
    val leaguespartanFontVar2: TextStyle,
    val leaguespartanFontVar3: TextStyle,
    val leaguespartanFontVar4: TextStyle,
    val leaguespartanFontVar5: TextStyle,
    val leaguespartanFontVar6: TextStyle,
)

val YummiziTypographies = YummiziTypography(
    teacherFontVar1 = TextStyle(
        fontFamily = FontFamily(Font(R.font.teachers_variable)),
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),

    teacherFontVar2 = TextStyle(
        fontFamily = FontFamily(Font(R.font.teachers_variable)),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),

    teacherFontVar3 = TextStyle(
        fontFamily = FontFamily(Font(R.font.teachers_variable)),
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),

    teacherFontVar4 = TextStyle(
        fontFamily = FontFamily(Font(R.font.teachers_variable)),
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),

    teacherFontVar5 = TextStyle(
        fontFamily = FontFamily(Font(R.font.teachers_variable)),
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp
    ),

    teacherFontVar6 = TextStyle(
        fontFamily = FontFamily(Font(R.font.teachers_variable)),
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    ),

    teacherFontVar7 = TextStyle(
        fontFamily = FontFamily(Font(R.font.teachers_variable)),
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    ),

    teacherFontVar8 = TextStyle(
        fontFamily = FontFamily(Font(R.font.teachers_variable)),
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    ),

    teacherFontVar9 = TextStyle(
        fontFamily = FontFamily(Font(R.font.teachers_variable)),
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),

    comfortaaFontVar1 = TextStyle(
        fontFamily = FontFamily(Font(R.font.comfortaa)),
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),

    comfortaaFontVar2 = TextStyle(
        fontFamily = FontFamily(Font(R.font.comfortaa)),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),

    comfortaaFontVar3 = TextStyle(
        fontFamily = FontFamily(Font(R.font.comfortaa)),
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp
    ),

    comfortaaFontVar4 = TextStyle(
        fontFamily = FontFamily(Font(R.font.comfortaa)),
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp
    ),

    comfortaaFontVar5 = TextStyle(
        fontFamily = FontFamily(Font(R.font.comfortaa)),
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    ),

    comfortaaFontVar6 = TextStyle(
        fontFamily = FontFamily(Font(R.font.comfortaa)),
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp
    ),

    leaguespartanFontVar1 = TextStyle(
        fontFamily = FontFamily(Font(R.font.league_spartan)),
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp
    ),

    leaguespartanFontVar2 = TextStyle(
        fontFamily = FontFamily(Font(R.font.league_spartan)),
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp
    ),

    leaguespartanFontVar3 = TextStyle(
        fontFamily = FontFamily(Font(R.font.league_spartan)),
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp
    ),

    leaguespartanFontVar4 = TextStyle(
        fontFamily = FontFamily(Font(R.font.league_spartan)),
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    ),

    leaguespartanFontVar5 = TextStyle(
        fontFamily = FontFamily(Font(R.font.league_spartan)),
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp
    ),

    leaguespartanFontVar6 = TextStyle(
        fontFamily = FontFamily(Font(R.font.league_spartan)),
        fontWeight = FontWeight.Bold,
        fontSize = 55.sp
    )

)

val LocalYummiziTypography = staticCompositionLocalOf { YummiziTypographies }