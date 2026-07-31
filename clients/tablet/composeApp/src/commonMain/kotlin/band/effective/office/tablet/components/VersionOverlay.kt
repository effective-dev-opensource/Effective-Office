package band.effective.office.tablet.components

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
import androidx.compose.foundation.layout.BoxScope
import band.effective.office.tablet.core.ui.platform.ScaledUiDensity
import band.effective.office.tablet.core.ui.platform.UiScaleDiagnostics
import kotlin.math.roundToInt

/**
 * Версия сборки, а рядом — метрики экрана: каким система отдала окно (`win`, px), какие
 * плотность и масштаб шрифта она выставила (`d`, `fs`) и во что их превратил
 * [ScaledUiDensity] (`ui` + фактические px содержимого).
 *
 * На Авроре это единственный способ увидеть, что именно приехало от системы: подобрать
 * вёрстку под чужое устройство, гадая про масштаб, не выйдет.
 *
 * Оверлей должен вызываться СНАРУЖИ [ScaledUiDensity] — иначе `d`/`fs` показывали бы уже
 * подменённые значения вместо системных, ради которых строка и добавлена.
 */
@Composable
fun BoxScope.VersionOverlay(
    modifier: Modifier = Modifier,
    text: String = "v${BuildKonfig.VERSION_NAME}",
) {
    val density = LocalDensity.current
    val windowSize = LocalWindowInfo.current.containerSize
    val appliedDensity = UiScaleDiagnostics.appliedDensity
    val contentPx = UiScaleDiagnostics.contentPx

    val diagnostics = buildString {
        append(text)
        append(" · win ").append(windowSize.width).append('x').append(windowSize.height)
        append(" · d ").append(density.density.twoDecimals())
        append(" fs ").append(density.fontScale.twoDecimals())
        if (appliedDensity != null) {
            append(" · ui ").append(appliedDensity.twoDecimals())
            append(' ').append(contentPx.width).append('x').append(contentPx.height)
        }
    }

    Text(
        text = diagnostics,
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

// String.format в общем коде нет, а Float.toString дал бы «1.4666667».
private fun Float.twoDecimals(): String {
    val hundredths = (this * 100).roundToInt()
    return "${hundredths / 100}.${(hundredths % 100).toString().padStart(2, '0')}"
}

