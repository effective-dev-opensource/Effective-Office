package band.effective.office.tv.feature.stories.presentation.rating.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import band.effective.office.tv.core.ui.theme.LocalTvSizes

/**
 * Generic rating screen layout with title, logo and content.
 * Used for Duolingo, Sport, and Supernova ratings.
 */
@Composable
fun RatingScreen(
    backgroundColor: Color,
    title: String,
    logo: Painter?,
    logoModifier: Modifier = Modifier,
    ratingTop: @Composable () -> Unit
) {
    val sizes = LocalTvSizes.current

    Box(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = sizes.paddingXLarge,
                    end = sizes.paddingHorizontalScreen
                )
        ) {
            TitleRating(
                title = title,
                logo = logo,
                logoModifier = logoModifier
            )
            Spacer(modifier = Modifier.height(sizes.gapXLarge))
            ratingTop()
        }
    }
}

