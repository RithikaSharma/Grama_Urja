package com.gramaurja.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Green = Color(0xFF16A34A)
private val GreenDark = Color(0xFF15803D)
private val Amber = Color(0xFFF59E0B)
private val Bg = Color(0xFFF8FAF7)
private val Surface = Color(0xFFFFFFFF)
private val OnDark = Color(0xFF0F172A)

private val Light = lightColorScheme(
    primary = Green, onPrimary = Color.White,
    secondary = Amber, onSecondary = OnDark,
    background = Bg, onBackground = OnDark,
    surface = Surface, onSurface = OnDark,
)
private val Dark = darkColorScheme(
    primary = GreenDark, onPrimary = Color.White,
    secondary = Amber, onSecondary = OnDark,
    background = Color(0xFF0B0F0C), onBackground = Color(0xFFE5E7EB),
    surface = Color(0xFF111613), onSurface = Color(0xFFE5E7EB),
)

@Composable
fun GramaUrjaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, content = content)
}

object Brand {
    val PowerOn = Color(0xFF22C55E)
    val PowerOff = Color(0xFF334155)
    val OnPowerOn = Color.White
    val OnPowerOff = Color.White
}
