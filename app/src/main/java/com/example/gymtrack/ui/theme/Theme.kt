package com.example.gymtrack.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GymColorScheme = darkColorScheme(
    primary = NeonGreen,
    secondary = GymGray,
    tertiary = NeonOrange,
    background = GymBlack,
    surface = GymDarkGray,
    onPrimary = GymBlack,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = GymWhite,
    onSurface = GymWhite
)

@Composable
fun GymTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled to maintain the "Gym" aesthetic strictly
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // We'll use a dark-centric theme as it's common for "Gym/Pro" apps
    val colorScheme = GymColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}