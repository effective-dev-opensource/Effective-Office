package band.effective.office.tv.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AppColors {
    val Background = Color(0xFF282828)
    val ButtonPrimary = Color(0xFFE85B0F)
    val ButtonSecondary = Color(0xFF434040)
    val White = Color(0xFFFFFFFF)
    val PurpleGradient = Color(0xFF5800CB)
    val OrangeGradient = Color(0xFFFE7B1C)
}

/**
 * Custom color palette data class for additional colors
 */
data class CustomColorsPalette(
    val elevationBackground: Color = Color.Unspecified,
    val orangeGradient: Color = Color.Unspecified,
    val purpleGradient: Color = Color.Unspecified,
    val buttonPrimary: Color = Color.Unspecified,
    val buttonSecondary: Color = Color.Unspecified,
    val primaryTextAndIcon: Color = Color.Unspecified
)

/**
 * Composition local for providing custom colors palette
 */
val LocalCustomColorsPalette = staticCompositionLocalOf { CustomColorsPalette() }

val CustomColors = CustomColorsPalette(
    elevationBackground = AppColors.Background,
    orangeGradient = AppColors.OrangeGradient,
    purpleGradient = AppColors.PurpleGradient,
    buttonPrimary = AppColors.ButtonPrimary,
    buttonSecondary = AppColors.ButtonSecondary,
    primaryTextAndIcon = AppColors.White
)

data class AppSizes(
    val titleWidth: Dp,
    val descriptionWidth: Dp,
    val buttonHeight: Dp,
    val startButtonWidth: Dp,
    val settingsButtonWidth: Dp,
    val gapSmall: Dp,
    val gapMedium: Dp,
    val gapLarge: Dp,
    val blurYOffset: Dp
)

val DefaultAppSizes = AppSizes(
    titleWidth = 635.dp,
    descriptionWidth = 500.dp,
    buttonHeight = 50.dp,
    startButtonWidth = 220.dp,
    settingsButtonWidth = 200.dp,
    gapSmall = 10.dp,
    gapMedium = 20.dp,
    gapLarge = 30.dp,
    blurYOffset = 400.dp
)

val LocalAppSizes = staticCompositionLocalOf { DefaultAppSizes }

/** Local providers for shapes and typography to allow overriding per-composition */
val LocalAppShapes = staticCompositionLocalOf {
    Shapes(
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(40.dp),
        large = RoundedCornerShape(210.dp)
    )
}

val LocalAppTypography = staticCompositionLocalOf {
    Typography()
}

object AppTheme {
    val sizes: AppSizes
        @Composable
        get() = LocalAppSizes.current

    val colors: CustomColorsPalette
        @Composable
        get() = LocalCustomColorsPalette.current

    val shapes: Shapes
        @Composable
        get() = LocalAppShapes.current

    val typography: Typography
        @Composable
        get() = LocalAppTypography.current
}

private val AppColorScheme = lightColorScheme(
    background = AppColors.Background,
    primary = AppColors.ButtonPrimary,
    secondary = AppColors.ButtonSecondary,
    surface = AppColors.Background,
    onPrimary = AppColors.White,
    onBackground = AppColors.White,
    onSurface = AppColors.White,
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = AppColorScheme

    val shapes = Shapes(
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(40.dp),
        large = RoundedCornerShape(210.dp)
    )

    val typography = Typography()

    val sizes = DefaultAppSizes

    val customColors = CustomColors

    CompositionLocalProvider(
        LocalAppSizes provides sizes,
        LocalCustomColorsPalette provides customColors,
        LocalAppShapes provides shapes,
        LocalAppTypography provides typography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}