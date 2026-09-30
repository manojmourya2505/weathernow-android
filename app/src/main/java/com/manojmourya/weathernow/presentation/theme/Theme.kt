package com.manojmourya.weathernow.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = SkyBlue40,
    onPrimary = Surface99,
    primaryContainer = SkyBlue90,
    onPrimaryContainer = SkyBlue20,
    secondary = SunAmber40,
    onSecondary = Surface99,
    secondaryContainer = SunAmber90,
    onSecondaryContainer = SunAmber20,
    background = Surface99,
    onBackground = OnSurface10,
    surface = Surface99,
    onSurface = OnSurface10,
    error = ErrorRed40,
)

private val DarkColors = darkColorScheme(
    primary = SkyBlue80,
    onPrimary = SkyBlue20,
    primaryContainer = SkyBlue20,
    onPrimaryContainer = SkyBlue90,
    secondary = SunAmber80,
    onSecondary = SunAmber20,
    secondaryContainer = SunAmber20,
    onSecondaryContainer = SunAmber90,
    background = Surface10,
    onBackground = OnSurface90,
    surface = Surface10,
    onSurface = OnSurface90,
    error = ErrorRed80,
)

@Composable
fun WeatherNowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WeatherNowTypography,
        content = content,
    )
}
