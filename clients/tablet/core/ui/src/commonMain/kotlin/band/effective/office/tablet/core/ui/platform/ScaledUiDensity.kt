package band.effective.office.tablet.core.ui.platform

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.min

/**
 * Приводит dp-пространство содержимого к [uiScaleBaseline] по короткой стороне: подменяет
 * [LocalDensity] на `доступные_px_короткой_стороны / uiScaleBaseline`. Тогда вёрстка в dp
 * занимает одну и ту же долю экрана независимо от того, какой масштаб отдала система.
 * Там, где масштабирование выключено (Android/iOS) — no-op.
 *
 * `fontScale` фиксируем в 1: системный масштаб шрифта иначе умножался бы на наш и уводил
 * текст от вёрстки.
 *
 * Размер берём из собственных constraints, а не из `LocalWindowInfo.containerSize`, чтобы
 * учитывать уже вычтенные отступы (статус-бар) и одинаково работать в окне модалки.
 */
@Composable
fun ScaledUiDensity(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (uiScaleBaseline <= 0.dp) {
        content()
        return
    }
    BoxWithConstraints(modifier = modifier) {
        val shortSidePx = min(constraints.maxWidth, constraints.maxHeight)
        if (!constraints.hasBoundedWidth || !constraints.hasBoundedHeight || shortSidePx <= 0) {
            content()
            return@BoxWithConstraints
        }
        val scaledDensity = shortSidePx / uiScaleBaseline.value
        SideEffect {
            UiScaleDiagnostics.appliedDensity = scaledDensity
            UiScaleDiagnostics.contentPx = IntSize(constraints.maxWidth, constraints.maxHeight)
        }
        CompositionLocalProvider(
            LocalDensity provides Density(density = scaledDensity, fontScale = 1f),
        ) {
            content()
        }
    }
}

/**
 * Что [ScaledUiDensity] реально посчитал — только для отладочной строки в оверлее.
 * Читать это надо СНАРУЖИ [ScaledUiDensity], иначе выводились бы уже подменённые значения,
 * а не системные.
 */
object UiScaleDiagnostics {
    var appliedDensity by mutableStateOf<Float?>(null)
    var contentPx by mutableStateOf(IntSize.Zero)
}
