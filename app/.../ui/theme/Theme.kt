package com.anonymousbeta.lakey.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LakeyColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = BlackDeep,
    secondary = GoldDark,
    onSecondary = TextPrimary,
    background = BlackDeep,
    onBackground = TextPrimary,
    surface = BlackCard,
    onSurface = TextPrimary,
    surfaceVariant = BlackSoft,
    onSurfaceVariant = TextSecondary
)

@Composable
fun LakeyTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BlackDeep.toArgb()
            window.navigationBarColor = BlackDeep.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = LakeyColorScheme,
        typography = Typography,
        content = content
    )
}
