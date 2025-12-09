package band.effective.office.tv.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Custom color palette data class for TV.
 * Names based on usage/purpose, not color values.
 */
data class TvColorsPalette(
    // Core UI colors
    val primary: Color = Color.Unspecified,           // Main brand color (orange) - buttons, accents
    val onPrimary: Color = Color.Unspecified,         // Text/icons on primary background
    val secondary: Color = Color.Unspecified,         // Secondary elements (gray buttons)
    val background: Color = Color.Unspecified,        // Screen background
    val surface: Color = Color.Unspecified,           // Card/container background
    
    // Text colors
    val textPrimary: Color = Color.Unspecified,       // Main text color
    val textSecondary: Color = Color.Unspecified,     // Secondary/muted text
    
    // Gradient colors (for decorative circles)
    val gradientOrange: Color = Color.Unspecified,
    val gradientPurple: Color = Color.Unspecified,
    
    // Story screen colors
    val storyBackground: Color = Color.Unspecified,
    val storyIndicator: Color = Color.Unspecified,
    val storyActiveIndicator: Color = Color.Unspecified,
    val storyEventFont: Color = Color.Unspecified,
    
    // Feature-specific colors
    val sport: Color = Color.Unspecified,
    val supernova: Color = Color.Unspecified,
    val duolingo: Color = Color.Unspecified,
    val duolingoDayStreak: Color = Color.Unspecified,
)

/**
 * Composition local for providing custom colors palette
 */
val LocalTvColorsPalette = staticCompositionLocalOf { TvColorsPalette() }

/**
 * TV colors palette instance
 */
val TvColors = TvColorsPalette(
    // Core UI colors
    primary = orangePrimary,
    onPrimary = md_theme_dark_onPrimary,
    secondary = md_theme_dark_surfaceVariant,
    background = md_theme_dark_background,
    surface = md_theme_dark_surface,
    
    // Text colors
    textPrimary = md_theme_dark_primaryTextAndIcon,
    textSecondary = md_theme_dark_secondaryTextAndIcon,
    
    // Gradient colors
    gradientOrange = orangeGradient,
    gradientPurple = purplePrimary,
    
    // Story screen colors
    storyBackground = storyBackground,
    storyIndicator = storyIndicator,
    storyActiveIndicator = storyActiveIndicator,
    storyEventFont = storyEventFont,
    
    // Feature colors
    sport = sportColor,
    supernova = supernovaColor,
    duolingo = duolingoColor,
    duolingoDayStreak = duolingoDayStreakColor,
)

/**
 * Material color scheme for TV (dark theme)
 */
private val TvColorScheme = darkColorScheme(
    background = md_theme_dark_background,
    primary = md_theme_dark_primary,
    secondary = md_theme_dark_secondary,
    surface = md_theme_dark_surface,
    onPrimary = md_theme_dark_onPrimary,
    onBackground = md_theme_dark_primaryTextAndIcon,
    onSurface = md_theme_dark_primaryTextAndIcon,
)

/**
 * Main TV application theme.
 */
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val typography = tvTypography()

    CompositionLocalProvider(
        LocalTvSizes provides DefaultTvSizes,
        LocalTvColorsPalette provides TvColors,
        LocalTvShapes provides TvShapes,
        LocalTvTypography provides typography
    ) {
        MaterialTheme(
            colorScheme = TvColorScheme,
            typography = typography,
            shapes = TvShapes,
            content = content
        )
    }
}