package band.effective.office.tablet.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import band.effective.office.tablet.BuildKonfig
import band.effective.office.tablet.platform.showsDebugMetrics
import kotlin.math.roundToInt

private const val HUNDREDTHS_IN_UNIT = 100
private const val FRACTION_DIGITS = 2

/**
 * Build version in the corner. Where [showsDebugMetrics] is on, it also carries the window size the
 * system handed over (`win`, px), the density (`d`) and the font scale (`fs`).
 */
@Composable
fun BoxScope.VersionOverlay(
    modifier: Modifier = Modifier,
    text: String = "v${BuildKonfig.VERSION_NAME}",
) {
    val overlayText = if (showsDebugMetrics) {
        val density = LocalDensity.current
        val windowSize = LocalWindowInfo.current.containerSize
        buildString {
            append(text)
            append(" · win ").append(windowSize.width).append(" x ").append(windowSize.height)
            append(" · d ").append(density.density.twoDecimals())
            append(" fs ").append(density.fontScale.twoDecimals())
        }
    } else {
        text
    }

    Text(
        text = overlayText,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
        textAlign = TextAlign.Start,
        modifier = modifier
            .align(Alignment.BottomEnd)
            .padding(
                end = 40.dp,
                bottom = 10.dp
            )
    )
}

// There is no String.format in common code, and Float.toString would print "1.4666667".
private fun Float.twoDecimals(): String {
    val hundredths = (this * HUNDREDTHS_IN_UNIT).roundToInt()
    val fraction = (hundredths % HUNDREDTHS_IN_UNIT).toString().padStart(FRACTION_DIGITS, '0')
    return "${hundredths / HUNDREDTHS_IN_UNIT}.$fraction"
}
