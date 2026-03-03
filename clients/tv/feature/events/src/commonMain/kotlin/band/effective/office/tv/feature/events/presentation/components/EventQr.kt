package band.effective.office.tv.feature.events.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import band.effective.office.tv.feature.events.Res
import band.effective.office.tv.feature.events.events_qr_content_description
import band.effective.office.tv.feature.events.events_registration_caption
import band.effective.office.tv.feature.events.events_registration_label
import org.jetbrains.compose.resources.stringResource

private const val QR_MAX_WIDTH_RATIO = 0.9f
private const val TEXT_MIN_WIDTH_RATIO = 0.8f
private const val LEADER_ID_BASE_URL = "https://leader-id.ru"

private fun buildEventRegistrationUrl(eventId: Int): String =
    "$LEADER_ID_BASE_URL/events/$eventId"

@Composable
fun EventQr(
    eventId: Int,
    modifier: Modifier = Modifier
) {
    val sizes = LocalTvSizes.current

    BoxWithConstraints(modifier = modifier) {
        val qrSize = calculateQrSize(maxWidth, sizes.qrCodeSize)
        val qrUrl = buildEventRegistrationUrl(eventId)

        val painter = rememberQrPainter(
            content = qrUrl,
            size = qrSize,
            color = LocalTvColorsPalette.current.textPrimary,
            backgroundColor = LocalTvColorsPalette.current.background
        )
        Image(
            painter = painter,
            contentDescription = stringResource(Res.string.events_qr_content_description),
            modifier = Modifier.align(Alignment.CenterEnd).size(qrSize)
        )
    }
}

private fun calculateQrSize(maxWidth: Dp, desiredSize: Dp): Dp {
    val maxAllowedSize = maxWidth * QR_MAX_WIDTH_RATIO
    return if (desiredSize > maxAllowedSize) maxAllowedSize else desiredSize
}

@Composable
private fun RegistrationInfo(
    modifier: Modifier = Modifier
) {
    val typography = LocalTvTypography.current
    val colors = LocalTvColorsPalette.current

    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.events_registration_caption),
            style = typography.bodyMedium,
            color = colors.textSecondary
        )
        Text(
            text = stringResource(Res.string.events_registration_label),
            style = typography.titleMedium,
            color = colors.textPrimary
        )
    }
}
