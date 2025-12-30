package band.effective.office.tv.feature.events.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.feature.events.domain.model.EventInfo
import coil3.ImageLoader

private const val MAIN_INFO_WIDTH_RATIO = 0.35f
private const val PHOTO_WIDTH_RATIO = 0.65f
private const val ADDITIONAL_INFO_WIDTH_RATIO = 0.35f
private const val QR_WIDTH_RATIO = 0.65f
private const val TOP_ROW_HEIGHT_RATIO = 0.5f
private const val BOTTOM_ROW_HEIGHT_RATIO = 0.5f

@Composable
fun EventCard(
    eventInfo: EventInfo,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier
) {
    val sizes = LocalTvSizes.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(sizes.gapXLarge)
    ) {
        val spacerHeight = sizes.gapXLarge - sizes.gapSmall
        val availableHeight = maxHeight - spacerHeight
        val topRowHeight = availableHeight * TOP_ROW_HEIGHT_RATIO
        val bottomRowHeight = availableHeight * BOTTOM_ROW_HEIGHT_RATIO

        Column(modifier = Modifier.fillMaxSize()) {
            EventTopRow(
                eventInfo = eventInfo,
                imageLoader = imageLoader,
                modifier = Modifier.height(topRowHeight)
            )
            Spacer(modifier = Modifier.height(spacerHeight))
            EventBottomRow(
                eventInfo = eventInfo,
                modifier = Modifier.height(bottomRowHeight)
            )
        }
    }
}

@Composable
private fun EventTopRow(
    eventInfo: EventInfo,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        MainEventInfo(
            eventInfo = eventInfo,
            modifier = Modifier.fillMaxWidth(MAIN_INFO_WIDTH_RATIO)
        )
        EventPhoto(
            eventInfo = eventInfo,
            imageLoader = imageLoader,
            modifier = Modifier.fillMaxWidth(PHOTO_WIDTH_RATIO)
        )
    }
}

@Composable
private fun EventBottomRow(
    eventInfo: EventInfo,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AdditionalEventInfo(
            eventInfo = eventInfo,
            modifier = Modifier.fillMaxWidth(ADDITIONAL_INFO_WIDTH_RATIO)
        )
        EventQr(
            eventId = eventInfo.id,
            modifier = Modifier.fillMaxWidth(QR_WIDTH_RATIO)
        )
    }
}
