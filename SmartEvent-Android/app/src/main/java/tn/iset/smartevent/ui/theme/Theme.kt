package tn.iset.smartevent.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SmartEventColors = lightColorScheme(
    primary = Navy900,
    onPrimary = White,
    secondary = Slate500,
    onSecondary = White,
    tertiary = Slate300,
    background = Cloud,
    onBackground = Navy900,
    surface = White,
    onSurface = Navy900,
    surfaceVariant = Cloud,
    onSurfaceVariant = Slate500,
    outline = Slate300,
    error = StatusRed
)

@Composable
fun SmartEventTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SmartEventColors,
        content = content
    )
}
