package band.effective.foosball.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.effectivefoosball.R

val Roboto = FontFamily(
    fonts = listOf(
        Font(R.font.roboto_medium, FontWeight.Medium)
    )
)
val DrukWide = FontFamily(
    fonts = listOf(
        Font(R.font.druk_wide_medium_regular, FontWeight.Normal)
    )
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 200.sp,
        color = White
    ),
    displayMedium = TextStyle(
        fontFamily = DrukWide,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        color = White
    ),
    labelLarge = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        color = White
    ),
    labelSmall = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = White
    ),
    bodyLarge = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = White
    )
)
