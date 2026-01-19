package band.effective.office.tv.feature.stories.presentation.rating.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.core.ui.theme.LocalTvTypography

@Composable
fun TitleRating(
    title: String,
    logo: Painter?,
    modifier: Modifier = Modifier,
    logoModifier: Modifier = Modifier,
) {
    val typography = LocalTvTypography.current
    val colors = LocalTvColorsPalette.current
    val sizes = LocalTvSizes.current

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        if (logo != null) {
            Image(
                modifier = logoModifier.size(sizes.ratingIconSize),
                painter = logo,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(sizes.gapLarge))
        }
        Text(
            text = title,
            style = typography.titleMedium.copy(
                fontWeight = FontWeight.Normal,
            ),
            color = colors.textPrimary
        )
    }
}

