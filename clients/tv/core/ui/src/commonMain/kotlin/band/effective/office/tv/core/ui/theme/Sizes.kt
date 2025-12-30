package band.effective.office.tv.core.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Size specifications for TV UI elements.
 */
data class TvSizes(
    // Menu content dimensions
    val menuTitleWidth: Dp = 635.dp,
    val menuDescriptionWidth: Dp = 400.dp,
    
    // Button dimensions
    val buttonHeightSmall: Dp = 50.dp,
    val buttonHeightMedium: Dp = 60.dp,
    val buttonHeightLarge: Dp = 100.dp,
    val startButtonWidth: Dp = 210.dp,
    val menuButtonWidth: Dp = 190.dp,
    val playButtonWidth: Dp = 160.dp,
    val progressButtonWidth: Dp = 300.dp,
    
    // Menu item dimensions
    val menuItemIconSize: Dp = 40.dp,
    // Generic small icon
    val iconSmall: Dp = 24.dp,
    // Play button icon
    val playButtonIconSize: Dp = 18.dp,
    val menuItemTextWidth: Dp = 150.dp,
    
    // Rating dimensions
    val ratingAvatarSize: Dp = 45.dp,
    val ratingAvatarLarge: Dp = 64.dp,
    val ratingIconSize: Dp = 32.dp,
    val ratingSmallIconSize: Dp = 15.dp,
    val ratingPlaceBadgeSize: Dp = 20.dp,
    
    // Story dimensions
    val storyIndicatorHeight: Dp = 8.dp,
    val storyIndicatorSpacing: Dp = 8.dp,
    val storyAvatarSize: Dp = 400.dp,
    val storyTextBlockWidth: Dp = 500.dp,
    val storyContentPaddingVertical: Dp = 64.dp,
    
    // Event dimensions
    val eventIconSize: Dp = 19.dp,
    val eventLargeIconSize: Dp = 29.dp,
    
    // Loading
    val loadCircleSize: Dp = 100.dp,
    val loadIconSize: Dp = 50.dp,
    val loadDotRadius: Dp = 5.dp,
    val loadDotCount: Int = 12,
    
    // QR code
    val qrCodeSize: Dp = 150.dp,
    
    // Spacing (gaps)
    val gapTiny: Dp = 2.dp,
    val gapSmall: Dp = 5.dp,
    val gapMedium: Dp = 10.dp,
    val gapLarge: Dp = 20.dp,
    val gapXLarge: Dp = 30.dp,
    val gapXXLarge: Dp = 50.dp,
    
    // Border thickness tokens
    val borderThin: Dp = 2.dp,
    val borderRegular: Dp = 4.dp,
    
    // Padding
    val paddingSmall: Dp = 10.dp,
    val paddingMedium: Dp = 20.dp,
    val paddingLarge: Dp = 30.dp,
    val paddingXLarge: Dp = 50.dp,
    val paddingHorizontalScreen: Dp = 100.dp,
    // Screen specific paddings
    val paddingSettingsScreen: Dp = 25.dp,
    // Card padding
    val cardPadding: Dp = 16.dp,
    
    // Gradient/blur
    val blurCircleOffset: Dp = 150.dp,
)

/**
 * Default TV sizes
 */
val DefaultTvSizes = TvSizes()

/**
 * Composition local for providing sizes
 */
val LocalTvSizes = staticCompositionLocalOf { DefaultTvSizes }
