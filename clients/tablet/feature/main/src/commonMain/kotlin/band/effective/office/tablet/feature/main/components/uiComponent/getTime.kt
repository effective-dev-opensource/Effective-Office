package band.effective.office.tablet.feature.main.components.uiComponent

import androidx.compose.runtime.Composable
import band.effective.office.tablet.core.domain.util.getCorrectDeclension
import band.effective.office.tablet.feature.main.Res
import band.effective.office.tablet.feature.main.hour_genitive
import band.effective.office.tablet.feature.main.hour_nominative
import band.effective.office.tablet.feature.main.hour_plural
import band.effective.office.tablet.feature.main.minuit_genitive
import band.effective.office.tablet.feature.main.minuit_nominative
import band.effective.office.tablet.feature.main.minuit_plural
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun Int.getDuration(): String {
    val min = this % 60
    val hours = this / 60
    val minStr = "$min ${
        getCorrectDeclension(
            number = min,
            nominativeCase = stringResource(Res.string.minuit_nominative),
            genitive = stringResource(Res.string.minuit_genitive),
            genitivePlural = stringResource(Res.string.minuit_plural),
        )
    }"
    val hourStr = "$hours ${
        getCorrectDeclension(
            number = hours,
            nominativeCase = stringResource(Res.string.hour_nominative),
            genitive = stringResource(Res.string.hour_genitive),
            genitivePlural = stringResource(Res.string.hour_plural),
        )
    }"
    return when {
        hours == 0 -> minStr
        min == 0 -> hourStr
        else -> "$hourStr $minStr"
    }
}