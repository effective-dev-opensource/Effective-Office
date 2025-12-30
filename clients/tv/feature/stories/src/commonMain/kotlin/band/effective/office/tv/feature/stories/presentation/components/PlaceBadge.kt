package band.effective.office.tv.feature.stories.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import band.effective.office.tv.core.ui.theme.LocalTvSizes

/**
 * Small circular place badge that overlaps avatar.
 */
@Composable
fun PlaceBadge(
    place: Int,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val sizes = LocalTvSizes.current
    val typography = LocalTvTypography.current

    Box(
        modifier = modifier
            .size(sizes.ratingPlaceBadgeSize)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = place.toString(),
            color = textColor,
            style = typography.bodySmall
        )
    }
}
