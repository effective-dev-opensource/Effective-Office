package band.effective.office.tv.core.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import band.effective.office.tv.core.ui.theme.robotoFontFamily

/**
 * Placeholder screen for features that are not yet implemented.
 */
@Composable
fun PlaceholderScreen(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTvColorsPalette.current
    val typography = LocalTvTypography.current
    val sizes = LocalTvSizes.current

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = typography.displaySmall.copy(fontFamily = robotoFontFamily()),
            color = colors.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(sizes.gapMedium))

        Text(
            text = subtitle,
            style = typography.bodyLarge,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }

}
