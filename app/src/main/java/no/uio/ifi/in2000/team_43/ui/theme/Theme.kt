package no.uio.ifi.in2000.team_43.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3B7D9C),
    onPrimary = Color.White,

    secondary = Color(0xFFE8CA89),
    onSecondary = Color(0xFF1C1B1F),

    tertiary = Color(0xFF9C2D17),
    onTertiary = Color.White,

    background = Color(0xFF121212),
    onBackground = Color(0xFFF1F1F1),

    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFF1F1F1)
)


private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF5AA9E6),     // En klar og frisk blå
    onPrimary = Color.White,

    secondary = Color(0xFFE1B964), // En dempet gyllen karamellfarge
    onSecondary = Color(0xFF1C1B1F),

    tertiary = Color(0xFFEE6C4D),    // En varm rød-oransje som gir kontrast
    onTertiary = Color.White,

    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),

    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F)
)




@Composable
fun Team_43Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}