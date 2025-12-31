package band.effective.office.tv.core.ui.model

import androidx.compose.runtime.Composable
import band.effective.office.tv.core.ui.Res
import band.effective.office.tv.core.ui.autoplay_placeholder_events_subtitle
import band.effective.office.tv.core.ui.autoplay_placeholder_photos_subtitle
import band.effective.office.tv.core.ui.autoplay_placeholder_stories_subtitle
import band.effective.office.tv.core.ui.autoplay_placeholder_unknown_subtitle
import band.effective.office.tv.core.ui.autoplay_placeholder_unknown_title
import band.effective.office.tv.core.ui.category_events
import band.effective.office.tv.core.ui.category_photos
import band.effective.office.tv.core.ui.category_stories
import org.jetbrains.compose.resources.stringResource

data class PlaceholderTexts(
    val title: String,
    val subtitle: String,
)

/**
 * Provides placeholder title/subtitle for autoplay by category.
 *
 * Keeps UI texts in resources and centralizes formatting for PHOTOS, EVENTS, STORIES.
 */
@Composable
fun ContentCategory?.placeholderTexts(
    statusText: String,
    playStateText: String,
): PlaceholderTexts = when (this) {
    ContentCategory.PHOTOS -> PlaceholderTexts(
        title = stringResource(Res.string.category_photos),
        subtitle = stringResource(
            Res.string.autoplay_placeholder_photos_subtitle,
            statusText,
            playStateText
        )
    )

    ContentCategory.EVENTS -> PlaceholderTexts(
        title = stringResource(Res.string.category_events),
        subtitle = stringResource(
            Res.string.autoplay_placeholder_events_subtitle,
            statusText,
            playStateText
        )
    )

    ContentCategory.STORIES -> PlaceholderTexts(
        title = stringResource(Res.string.category_stories),
        subtitle = stringResource(
            Res.string.autoplay_placeholder_stories_subtitle,
            statusText,
            playStateText
        )
    )

    null -> PlaceholderTexts(
        title = stringResource(Res.string.autoplay_placeholder_unknown_title),
        subtitle = stringResource(
            Res.string.autoplay_placeholder_unknown_subtitle,
            statusText
        )
    )
}

