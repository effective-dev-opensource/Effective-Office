package band.effective.office.tv.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp

/**
 * Gradient circle for background.
 */
@Composable
fun CircleWithBlur(
    modifier: Modifier = Modifier,
    xOffset: Dp,
    yOffset: Dp,
    color: Color
) {
    Canvas(
        modifier = modifier
            .offset(xOffset, yOffset)
            .fillMaxSize(),
        onDraw = {
            val radius = size.width / 4
            scale(2f) {
                drawCircle(
                    Brush.radialGradient(
                        colors = listOf(color, Color.Transparent),
                        radius = radius
                    )
                )
            }
        }
    )
}
