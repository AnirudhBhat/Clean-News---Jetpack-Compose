package com.abhat.cleannews_compose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color.Black,
    primaryVariant = Gray850,

    onPrimary = White,

//    background = Color.Black,
//    surface = Color.Black,
//    onPrimary = White,
//    onSecondary = WhiteWithAlpha,
//    onBackground = Color.Black,
//    onSurface = Color.Black,
)

private val LightColorPalette = lightColors(
    primary = Color.White,
    primaryVariant = White,

    onPrimary = Color.Black,

//    background = Color.White,
//    surface = Color.White,
//    onPrimary = Gray850,
//    onSecondary = Gray600,
//    onBackground = Color.White,
//    onSurface = Color.White,
)

@Composable
fun CleanNewsComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}