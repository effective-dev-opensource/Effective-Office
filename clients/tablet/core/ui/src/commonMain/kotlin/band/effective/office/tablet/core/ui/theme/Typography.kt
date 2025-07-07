package band.effective.office.tablet.core.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography styles for the application
 * Defines a consistent set of text styles for different header levels
 */

/**
 * Header 1 - Largest header style (56sp)
 */
val header1 = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 56.sp,
    lineHeight = 56.sp * 1.5f,
    letterSpacing = 56.sp * 0.02f
)

/**
 * Header 2 - Very large header style (48sp)
 */
val header2 = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 48.sp,
    lineHeight = 48.sp * 1.5f,
    letterSpacing = 48.sp * 0.02f
)

/**
 * Header 3 - Large header style (36sp)
 */
val header3 = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 36.sp,
    lineHeight = 36.sp * 1.5f,
    letterSpacing = 36.sp * 0.02f
)

/**
 * Header 4 - Medium-large header style (28sp)
 */
val header4 = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 28.sp,
    lineHeight = 28.sp * 1.5f,
    letterSpacing = 28.sp * 0.02f
)

/**
 * Header 5 - Medium header style (24sp)
 */
val header5 = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 24.sp,
    lineHeight = 24.sp * 1.5f,
    letterSpacing = 24.sp * 0.02f
)

/**
 * Header 6 - Small-medium header style (20sp)
 */
val header6 = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 20.sp,
    lineHeight = 20.sp * 1.5f,
    letterSpacing = 20.sp * 0.02f
)

/**
 * Header 7 - Small header style (18sp)
 */
val header7 = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    lineHeight = 18.sp * 1.5f,
    letterSpacing = 18.sp * 0.02f
)

/**
 * Header 8 - Smallest header style (16sp)
 */
val header8 = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 16.sp * 1.5f,
    letterSpacing = 16.sp * 0.02f
)

/**
 * Button text style based on Header 6 with adjusted letter spacing
 */
val header6_button = header6.copy(letterSpacing = 20.sp * 0.01f)