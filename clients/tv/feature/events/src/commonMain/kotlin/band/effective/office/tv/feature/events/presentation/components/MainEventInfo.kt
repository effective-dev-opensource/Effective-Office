package band.effective.office.tv.feature.events.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
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
import band.effective.office.tv.core.ui.theme.AppTheme
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

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
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.height(sizes.gapLarge))

        LabelValueText(
            label = stringResource(Res.string.events_organizer_caption),
            value = eventInfo.organizer
                .takeUnless { it.isNullOrBlank() }
                ?: stringResource(Res.string.events_organizer_fallback),
            modifier = Modifier.padding(vertical = sizes.gapLarge - sizes.gapSmall),
        )
        if (eventInfo.speakers.isNotEmpty()) {
            LabelValueText(
                label = stringResource(Res.string.events_speakers_caption),
                value = speakersName(eventInfo.speakers),
                modifier = Modifier.padding(vertical = sizes.gapLarge - sizes.gapSmall),
            )
        }
    }
}

private fun speakersName(speakers: List<String>): String =
    speakers.take(3).joinToString(separator = ", ")

@Preview
@Composable
private fun MainEventInfoPreview() {
    AppTheme {
        MainEventInfo(
            eventInfo = EventInfo(
                id = 1,
                name = "Effective Dev Days",
                startDateTime = LocalDateTime(2024, 7, 10, 11, 0),
                finishDateTime = LocalDateTime(2024, 7, 10, 15, 30),
                isOnline = true,
                photoUrl = null,
                organizer = "Effective Team",
                speakers = listOf("Speaker 1", "Speaker 2", "Speaker 3"),
                location = "Office / Online"
            )
        )
    }
}
