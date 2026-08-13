package band.effective.office.tablet.feature.main.components.uiComponent

import androidx.compose.runtime.Composable
import band.effective.office.tablet.feature.main.Res
import band.effective.office.tablet.feature.main.hours
import band.effective.office.tablet.feature.main.minutes
import org.jetbrains.compose.resources.pluralStringResource

private const val NO_BREAK_SPACE = '\u00A0'

@Composable
internal fun Int.getDuration(): String {
    val min = this % 60
    val hours = this / 60
    val minStr = "$min$NO_BREAK_SPACE${pluralStringResource(Res.plurals.minutes, min)}"
    val hourStr = "$hours$NO_BREAK_SPACE${pluralStringResource(Res.plurals.hours, hours)}"
    return when {
        hours == 0 -> minStr
        min == 0 -> hourStr
        else -> "$hourStr $minStr"
    }
}