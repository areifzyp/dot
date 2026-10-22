package io.github.areifzyp.dot.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val DotTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = DotoFont,
        fontWeight = FontWeight.Black,
        fontSize = 57.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = DotoFont,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = DotoFont,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = DotoFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = DotoFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = DotoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = DotoFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
    ),
)