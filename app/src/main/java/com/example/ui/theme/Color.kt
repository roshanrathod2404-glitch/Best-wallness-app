package com.example.ui.theme

import androidx.compose.ui.graphics.Color

var isLightModeGlobal = false

val BgCharcoal: Color get() = if (isLightModeGlobal) Color(0xFFFAFAFA) else Color(0xFF1E1E1E)
val SurfaceDark: Color get() = if (isLightModeGlobal) Color(0xFFFFFFFF) else Color(0xFF2A2A2A)
val SurfaceVariant: Color get() = if (isLightModeGlobal) Color(0xFFF0F0F0) else Color(0xFF3D3D3D)
val TextWhite: Color get() = if (isLightModeGlobal) Color(0xFF121212) else Color(0xFFFFFFFF)
val TextSilver: Color get() = if (isLightModeGlobal) Color(0xFF6E6E6E) else Color(0xFFB0B3B8)
val AccentGreen = Color(0xFF00E676)
val AccentBlue = Color(0xFF2979FF)
val AccentOrange = Color(0xFFFF9100)
val BorderColor: Color get() = if (isLightModeGlobal) Color(0x1F000000) else Color(0x1FFFFFFF)


