package band.effective.office.tv.feature.events.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.Text
import band.effective.office.tv.core.ui.mic
import org.jetbrains.compose.resources.painterResource
import band.effective.office.tv.core.ui.Res as CoreRes
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import band.effective.office.tv.core.ui.user
import band.effective.office.tv.feature.events.Res
import band.effective.office.tv.feature.events.domain.model.EventInfo
import band.effective.office.tv.feature.events.events_organizer_caption
import band.effective.office.tv.feature.events.events_organizer_fallback
import band.effective.office.tv.feature.events.events_speakers_caption
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainEventInfo(
    eventInfo: EventInfo,
    modifier: Modifier = Modifier
) {
    val colors = LocalTvColorsPalette.current
    val typography = LocalTvTypography.current
    val sizes = LocalTvSizes.current

    Column(
        modifier = modifier
    ) {
        Text(
            text = eventInfo.name,
            color = colors.textPrimary,
            style = typography.headlineMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        TextWithCaptionAndIcon(
            iconPainter = painterResource(CoreRes.drawable.user),
            text = eventInfo.organizer
                .takeUnless { it.isNullOrBlank() }
                ?: stringResource(Res.string.events_organizer_fallback),
            caption = stringResource(Res.string.events_organizer_caption),
            iconSize = sizes.iconSmall,
            modifier = Modifier.padding(vertical = sizes.gapLarge - sizes.gapSmall),
            textStyle = typography.titleSmall,
            captionStyle = typography.bodyMedium,
            iconTint = colors.textPrimary
        )
        if (eventInfo.speakers.isNotEmpty()) {
            TextWithCaptionAndIcon(
                iconPainter = painterResource(CoreRes.drawable.mic),
                text = speakersName(eventInfo.speakers),
                caption = stringResource(Res.string.events_speakers_caption),
                iconSize = sizes.iconSmall,
                textStyle = typography.titleSmall,
                captionStyle = typography.bodyMedium,
                iconTint = colors.textPrimary
            )
        }
    }
}

private fun speakersName(speakers: List<String>): String =
    speakers.take(3).joinToString(separator = ", ")
