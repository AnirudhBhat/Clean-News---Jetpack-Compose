package com.abhat.cleannews_compose.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.abhat.cleannews_compose.R

private val newsFont = FontFamily(
    Font(R.font.robotomono_light, FontWeight.Light),
    Font(R.font.robotomono_regular, FontWeight.Medium)
)

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = newsFont,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = newsFont,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp
    ),
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)