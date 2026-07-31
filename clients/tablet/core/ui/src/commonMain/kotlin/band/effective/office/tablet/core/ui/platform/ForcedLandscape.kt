package band.effective.office.tablet.core.ui.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalDensity

/**
 * Разворачивает содержимое в альбом, если окно пришло портретным и [forceLandscape] включён:
 * бокс альбомного размера (H×W) центрируем и поворачиваем на 90° вокруг его центра — габарит
 * после поворота получается ровно W×H и точно закрывает экран. На альбомном окне и на
 * Android/iOS — no-op.
 *
 * Оборачивать нужно КАЖДЫЙ слой: `Popup` и `dialog<>` форка рендерятся отдельными сценами в
 * неповёрнутом окне, и от корня до них ничего не доходит.
 */
@Composable
fun ForcedLandscape(content: @Composable () -> Unit) {
    if (!forceLandscape) {
        content()
        return
    }
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        val w = constraints.maxWidth
        val h = constraints.maxHeight
        if (w >= h) {
            content()
        } else {
            val density = LocalDensity.current
            Box(
                modifier = Modifier
                    .requiredSize(
                        width = with(density) { h.toDp() },
                        height = with(density) { w.toDp() },
                    )
                    .rotate(90f),
            ) {
                content()
            }
        }
    }
}
