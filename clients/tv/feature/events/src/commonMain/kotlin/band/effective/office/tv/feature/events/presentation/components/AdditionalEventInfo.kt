package band.effective.office.tv.feature.events.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.painterResource
import band.effective.office.tv.core.ui.Res as CoreRes
import band.effective.office.shared.core.utils.currentLocalDateTime
import band.effective.office.tv.core.ui.clock
import band.effective.office.tv.core.ui.error_circle
import band.effective.office.tv.core.ui.location
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
                iconPainter = painterResource(CoreRes.drawable.error_circle),
                text = stringResource(Res.string.events_registration_ends_in, registrationText),
                caption = stringResource(Res.string.events_registration_caption),
                textStyle = typography.titleMedium.copy(fontWeight = FontWeight.Black),
                captionStyle = typography.bodyMedium,
                iconSize = sizes.eventLargeIconSize
            )
        }

        TextWithCaptionAndIcon(
            iconPainter = painterResource(CoreRes.drawable.clock),
            text = formatTimeRange(eventInfo.startDateTime, eventInfo.finishDateTime),
            textStyle = typography.titleMedium.copy(fontWeight = FontWeight.Black),
            captionStyle = typography.bodyMedium,
            iconSize = sizes.eventLargeIconSize
        )

        val locationText = if (!eventInfo.location.isNullOrBlank()) {
            eventInfo.location.trim()
        } else {
            if (eventInfo.isOnline) {
                stringResource(Res.string.events_location_online_default)
            } else {
                stringResource(Res.string.events_location_offline_default)
            }
        }
        TextWithCaptionAndIcon(
            iconPainter = painterResource(CoreRes.drawable.location),
            text = locationText,
            textStyle = typography.titleMedium.copy(fontWeight = FontWeight.Black),
            captionStyle = typography.bodyMedium,
            iconSize = sizes.eventLargeIconSize
        )
    }
}
