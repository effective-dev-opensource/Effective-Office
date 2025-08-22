package band.effective.office.tablet.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/**
 * Dark color scheme for the application
 */
private val darkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    secondary = md_theme_dark_secondary,
    background = md_theme_dark_background,
    surface = md_theme_dark_surface,
    error = md_theme_dark_onError,
    onPrimary = md_theme_dark_onPrimary
)

/**
 * Custom dark color palette for additional colors not covered by Material Theme
 */
val CustomDarkColors = CustomColorsPalette(
    elevationBackground = md_theme_dark_elevationBackground,
    mountainBackground = md_theme_dark_mountainBackground,
    busyStatus = md_theme_dark_busyStatus,
    freeStatus = md_theme_dark_freeStatus,
    onSuccess = md_theme_dark_onSuccess,
    secondaryButton = md_theme_dark_secondaryButton,
    primaryTextAndIcon = md_theme_dark_primaryTextAndIcon,
    secondaryTextAndIcon = md_theme_dark_secondaryTextAndIcon,
    tertiaryTextAndIcon = md_theme_dark_tertiaryTextAndIcon,
    pressedPrimaryButton = md_theme_dark_pressedPrimaryButton,
    disabledPrimaryButton = md_theme_dark_disabledPrimaryButton,
    loadingColor = md_theme_dark_surface
)

/**
 * Light color scheme for the application
 */
private val lightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    secondary = md_theme_light_secondary,
    background = md_theme_light_background,
    surface = md_theme_light_surface,
    error = md_theme_light_onError,
)

/**
 * Custom light color palette for additional colors not covered by Material Theme
 */
val CustomLightColors = CustomColorsPalette(
    elevationBackground = md_theme_light_elevationBackground,
    mountainBackground = md_theme_light_mountainBackground,
    busyStatus = md_theme_light_busyStatus,
    freeStatus = md_theme_light_freeStatus,
    onSuccess = md_theme_light_onSuccess,
    secondaryButton = md_theme_light_secondaryButton,
    primaryTextAndIcon = md_theme_light_primaryTextAndIcon,
    secondaryTextAndIcon = md_theme_light_secondaryTextAndIcon,
    tertiaryTextAndIcon = md_theme_light_tertiaryTextAndIcon,
    pressedPrimaryButton = md_theme_light_pressedPrimaryButton,
    disabledPrimaryButton = md_theme_light_disabledPrimaryButton,
    loadingColor = md_theme_light_surface
)

/**
 * Custom color palette data class for additional colors not covered by Material Theme
 */
@Immutable
 data class CustomColorsPalette(
    val elevationBackground: Color = Color.Unspecified,
    val mountainBackground: Color = Color.Unspecified,
    val busyStatus: Color = Color.Unspecified,
    val freeStatus: Color = Color.Unspecified,
    val onSuccess: Color = Color.Unspecified,
    val secondaryButton: Color = Color.Unspecified,
    val primaryTextAndIcon: Color = Color.Unspecified,
    val secondaryTextAndIcon: Color = Color.Unspecified,
    val tertiaryTextAndIcon: Color = Color.Unspecified,
    val pressedPrimaryButton: Color = Color.Unspecified,
    val disabledPrimaryButton: Color = Color.Unspecified,
    val parameterTitle: Color = Color.Unspecified,
    val loadingColor: Color = Color.Unspecified
)

/**
 * Composition local for providing custom colors palette
 */
val LocalCustomColorsPalette = staticCompositionLocalOf { CustomColorsPalette() }

/**
 * Main application theme
 *
 * @param useDarkTheme Whether to use dark theme (true) or light theme (false)
 * @param content Content to be styled with this theme
 */
@Composable
fun AppTheme(
    useDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (!useDarkTheme) {
        lightColorScheme
    } else {
        darkColorScheme
    }

    val customColorsPalette = if (!useDarkTheme) {
        CustomLightColors
    } else {
        CustomDarkColors
    }

    // Material 3 Typography uses different parameter names
    val typography = Typography(
        // Map our custom text styles to Material 3 Typography
        displayLarge = header1,
        displayMedium = header2,
        displaySmall = header3,
        headlineLarge = header4,
        headlineMedium = header5,
        headlineSmall = header6,
        titleLarge = header7,
        titleMedium = header8,
        titleSmall = header8,
        bodyLarge = header8,
        bodyMedium = header8,
        bodySmall = header8,
        labelLarge = header6_button,
        labelMedium = header8,
        labelSmall = header8
    )

    CompositionLocalProvider(
        LocalCustomColorsPalette provides customColorsPalette
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = {
                Surface(content = content)
            }
        )
    }
}

/**
 * Extension properties for Typography to access our custom text styles
 */
val Typography.h1: TextStyle
    get() = displayLarge

val Typography.h2: TextStyle
    get() = displayMedium

val Typography.h3: TextStyle
    get() = displaySmall

val Typography.h4: TextStyle
    get() = headlineLarge

val Typography.h5: TextStyle
    get() = headlineMedium

val Typography.h6: TextStyle
    get() = headlineSmall

val Typography.h7: TextStyle
    get() = titleLarge

val Typography.h8: TextStyle
    get() = titleMedium

val Typography.h6_button: TextStyle
    get() = labelLarge