package pe.cajamapp.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CajamarcaColors = lightColorScheme(
    primary = ValleyGreen,
    onPrimary = Color.White,
    primaryContainer = MaizeSoft,
    onPrimaryContainer = DeepValley,
    secondary = SecondaryAmber,
    onSecondary = Color.White,
    background = BackgroundLight,
    onBackground = TextTitle,
    surface = SurfacePureWhite,
    onSurface = TextTitle,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextMuted,
    outline = Color(0xFF817C6F),
    outlineVariant = BorderSubtle,
    secondaryContainer = MaizeSoft,
    onSecondaryContainer = TextTitle
)

@Composable
fun CajamAppTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SurfacePureWhite.toArgb()
            window.navigationBarColor = SurfacePureWhite.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = CajamarcaColors,
        typography = AppTypography,
        content = content
    )
}
