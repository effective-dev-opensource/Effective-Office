package band.effective.office.tv.feature.events.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Place
import band.effective.office.shared.core.utils.currentLocalDateTime
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import band.effective.office.tv.feature.events.Res
import band.effective.office.tv.feature.events.domain.model.EventInfo
import band.effective.office.tv.feature.events.events_registration_caption
import band.effective.office.tv.feature.events.events_location_online_default
import band.effective.office.tv.feature.events.events_location_offline_default
import band.effective.office.tv.feature.events.events_registration_ends_in
import band.effective.office.tv.feature.events.presentation.format.formatRegistrationEndsIn
import band.effective.office.tv.feature.events.presentation.format.formatTimeRange
import org.jetbrains.compose.resources.stringResource

@Composable
fun AdditionalEventInfo(
    eventInfo: EventInfo,
    modifier: Modifier = Modifier
) {
    val typography = LocalTvTypography.current
    val sizes = LocalTvSizes.current

    Column(
        modifier = modifier
            .padding(bottom = sizes.gapXXLarge, top = sizes.gapSmall),
        verticalArrangement = Arrangement.spacedBy(sizes.gapMedium)
    ) {
        val registrationText = eventInfo.endRegDate
            ?.takeIf { it > currentLocalDateTime }
            ?.let { formatRegistrationEndsIn(it) }
            ?.takeUnless { it.isBlank() }

        if (registrationText != null) {
            TextWithCaptionAndIcon(
                icon = Icons.Filled.CalendarToday,
                text = stringResource(Res.string.events_registration_ends_in, registrationText),
                caption = stringResource(Res.string.events_registration_caption),
                textStyle = typography.titleMedium.copy(fontWeight = FontWeight.Black),
                captionStyle = typography.bodyMedium,
                iconSize = sizes.eventLargeIconSize
            )
        }

        TextWithCaptionAndIcon(
            icon = Icons.Filled.AccessTime,
            text = formatTimeRange(eventInfo.startDateTime, eventInfo.finishDateTime),
            textStyle = typography.titleMedium.copy(fontWeight = FontWeight.Black),
            captionStyle = typography.bodyMedium,
            iconSize = sizes.eventLargeIconSize
        )

        val locationText = if (eventInfo.location != null && eventInfo.location.isNotBlank()) {
            eventInfo.location.trim()
        } else {
            if (eventInfo.isOnline) {
                stringResource(Res.string.events_location_online_default)
            } else {
                stringResource(Res.string.events_location_offline_default)
            }
        }
        TextWithCaptionAndIcon(
            icon = Icons.Filled.Place,
            text = locationText,
            textStyle = typography.titleMedium.copy(fontWeight = FontWeight.Black),
            captionStyle = typography.bodyMedium,
            iconSize = sizes.eventLargeIconSize
        )
    }
}
