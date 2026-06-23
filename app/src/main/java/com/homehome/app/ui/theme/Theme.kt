package com.homehome.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Purple90,
    onPrimaryContainer = Purple10,
    secondary = PurpleGrey40,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = PurpleGrey90,
    onSecondaryContainer = Purple10,
    tertiary = Pink40,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = Pink90,
    onTertiaryContainer = Pink40,
    background = SoftBackground,
    surface = SoftSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Purple20,
    primaryContainer = Purple30,
    onPrimaryContainer = Purple90,
    secondary = PurpleGrey80,
    onSecondary = PurpleGrey40,
    secondaryContainer = PurpleGrey40,
    tertiary = Pink80,
    onTertiary = Pink40,
    tertiaryContainer = Pink40,
    onTertiaryContainer = Pink90
)

@Composable
fun HomehomeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
