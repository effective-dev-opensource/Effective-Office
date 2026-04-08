package band.effective.office.tv.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import band.effective.office.tv.core.ui.Res
import band.effective.office.tv.core.ui.druktextwidelcg_medium
import band.effective.office.tv.core.ui.inter_black
import band.effective.office.tv.core.ui.inter_bold
import band.effective.office.tv.core.ui.inter_light
import band.effective.office.tv.core.ui.inter_regular
import band.effective.office.tv.core.ui.inter_thin
import band.effective.office.tv.core.ui.museocyrl
import band.effective.office.tv.core.ui.roboto_black
import band.effective.office.tv.core.ui.roboto_bold
import band.effective.office.tv.core.ui.roboto_light
import band.effective.office.tv.core.ui.roboto_medium
import band.effective.office.tv.core.ui.roboto_regular
import band.effective.office.tv.core.ui.roboto_thin
import org.jetbrains.compose.resources.Font

/**
 * Roboto font family.
 */
@Composable
fun robotoFontFamily(): FontFamily = FontFamily(
    Font(Res.font.roboto_black, weight = FontWeight.Black),
    Font(Res.font.roboto_bold, weight = FontWeight.Bold),
    Font(Res.font.roboto_medium, weight = FontWeight.Medium),
    Font(Res.font.roboto_regular, weight = FontWeight.Normal),
    Font(Res.font.roboto_light, weight = FontWeight.Light),
    Font(Res.font.roboto_thin, weight = FontWeight.Thin),
)

/**
 * Inter font family.
 */
@Composable
fun interFontFamily(): FontFamily = FontFamily(
    Font(Res.font.inter_black, weight = FontWeight.Black),
    Font(Res.font.inter_bold, weight = FontWeight.Bold),
    Font(Res.font.inter_regular, weight = FontWeight.Normal),
    Font(Res.font.inter_light, weight = FontWeight.Light),
    Font(Res.font.inter_thin, weight = FontWeight.Thin),
)

/**
 * Typography styles for application.
 */
@Composable
fun tvTypography(): Typography {
    val roboto = robotoFontFamily()
    val inter = interFontFamily()
    val druk = FontFamily(Font(Res.font.druktextwidelcg_medium))
    val museocyrl = FontFamily(Font(Res.font.museocyrl))
    return Typography(
        // LoadScreen title (largest)
        displayLarge = TextStyle(
            fontFamily = druk,
            fontWeight = FontWeight.Bold,
            fontSize = 60.sp,
            lineHeight = 68.sp,
        ),
        // Story employee name
        displayMedium = TextStyle(
            fontFamily = museocyrl,
            fontStyle = FontStyle.Italic,
            fontSize = 52.sp,
            lineHeight = 60.sp,
        ),
        // Menu title, settings title
        displaySmall = TextStyle(
            fontFamily = druk,
            fontWeight = FontWeight.Bold,
            fontSize = 50.sp,
            lineHeight = 58.sp,
        ),
        // Story body
        headlineLarge = TextStyle(
            fontFamily = druk,
            fontWeight = FontWeight.Bold,
            fontSize = 46.sp,
            lineHeight = 54.sp,
        ),
        // Menu items, event info
        headlineMedium = TextStyle(
            fontFamily = druk,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
        ),
        // No stories message
        headlineSmall = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            lineHeight = 34.sp,
        ),
        // h1
        titleLarge = TextStyle(
            fontFamily = druk,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 26.sp,
        ),
        // h3 (bold)
        titleMedium = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 26.sp,
        ),
        // h2
        titleSmall = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 22.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 22.sp,
        ),
        // Caption
        bodyMedium = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        // Smallest (badges)
        bodySmall = TextStyle(
            fontFamily = druk,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            lineHeight = 14.sp
        ),
        // Button label
        labelLarge = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 28.sp,
        ),
        // Button secondary
        labelMedium = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        // Caption
        labelSmall = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
    )
}

/**
 * Composition local for providing typography
 */
val LocalTvTypography = staticCompositionLocalOf { Typography() }
