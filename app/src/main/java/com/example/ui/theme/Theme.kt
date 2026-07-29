package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
  darkColorScheme(
    primary = AccentGreen,
    onPrimary = BgCharcoal,
    secondary = TextSilver,
    onSecondary = TextWhite,
    background = BgCharcoal,
    onBackground = TextWhite,
    surface = SurfaceDark,
    onSurface = TextWhite,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSilver
  )

private val LightColorScheme =
  lightColorScheme(
    primary = AccentGreen,
    onPrimary = TextWhite,
    secondary = AccentBlue,
    onSecondary = TextWhite,
    background = BgCharcoal,
    onBackground = TextWhite,
    surface = SurfaceDark,
    onSurface = TextWhite,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSilver
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  isLightModeGlobal = !darkTheme
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}


