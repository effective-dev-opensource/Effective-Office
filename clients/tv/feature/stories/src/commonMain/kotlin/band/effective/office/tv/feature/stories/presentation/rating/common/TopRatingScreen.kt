package band.effective.office.tv.feature.stories.presentation.rating.common

import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

/**
 * Wrapper component that combines RatingScreen and TopRating.
 * Provides a convenient API for displaying rating screens with title, logo, and top rating items.
 */
@Composable
fun <T> TopRatingScreen(
    users: List<T>,
    backgroundColor: Color,
    title: String,
    logo: Painter? = null,
    logoModifier: Modifier = Modifier,
    item: @Composable LazyGridItemScope.(T, index: Int) -> Unit
) {
    RatingScreen(
        backgroundColor = backgroundColor,
        title = title,
        logo = logo,
        logoModifier = logoModifier
    ) {
        TopRating(users = users, item = item)
    }
}