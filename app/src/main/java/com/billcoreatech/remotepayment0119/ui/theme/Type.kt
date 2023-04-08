package com.billcoreatech.remotepayment0119.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.billcoreatech.remotepayment0119.R

val fonts = FontFamily(
    Font(R.font.notosanskr_regular)
)

val fontsBold = FontFamily(
    Font(R.font.notosanskr_bold)
)

// Set of Material typography styles to start with
val typography = Typography(
    h1 = TextStyle(
        fontFamily = fontsBold,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        color = darkGray
    ),
    h2 = TextStyle(
        fontFamily = fontsBold,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        color = darkGray
    ),
    h3 = TextStyle(
        fontFamily = fontsBold,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        color = darkGray
    ),
    h4 = TextStyle(
        fontFamily = fontsBold,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = darkGray
    ),
    h5 = TextStyle(
        fontFamily = fontsBold,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = darkGray
    ),
    body1 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = darkGray
    ),
    body2 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = darkGray
    ),
    /* Other default text styles to override */
    button = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        color = Lime100
    ),
    caption = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = Lime100
    ),
    subtitle1 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = Lime100
    ),
    subtitle2 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = Lime100
    )

)