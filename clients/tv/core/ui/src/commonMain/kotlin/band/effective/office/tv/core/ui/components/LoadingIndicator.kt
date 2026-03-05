package band.effective.office.tv.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.translate
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import band.effective.office.tv.core.ui.theme.robotoFontFamily
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val ANIMATION_DELAY_MS = 500L

/**
 * Circular loading indicator with animated dots.
 * One dot "hides" and rotates around the circle.
 */
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    val colors = LocalTvColorsPalette.current
    val sizes = LocalTvSizes.current
    
    val size = sizes.loadCircleSize
    val dotCount = sizes.loadDotCount
    val dotRadius = sizes.loadDotRadius
    
    var ticks by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(ANIMATION_DELAY_MS)
            ticks++
        }
    }

    Box(modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Pair(this.size.width / 2, this.size.height / 2)
            val radius = this.size.width / 2
            val positions = calculateDotPositions(center, radius, dotCount)
            val inactiveIndex = ticks % dotCount

            translate(left = -this.size.width / 2) {
                positions.forEachIndexed { index, position ->
                    if (index != inactiveIndex) {
                        translate(left = position.first, top = position.second) {
                            drawCircle(
                                color = colors.textPrimary,
                                radius = dotRadius.toPx()
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Calculates positions for dots evenly distributed around a circle.
 */
private fun calculateDotPositions(
    center: Pair<Float, Float>,
    radius: Float,
    count: Int
): List<Pair<Float, Float>> {
    return List(count) { index ->
        val angle = PI + index * 2 * PI / count
        Pair(
            (center.first + radius * cos(angle)).toFloat(),
            (center.second + radius * sin(angle)).toFloat()
        )
    }
}

/**
 * Full-screen loading screen with title.
 *
 * @param title Screen name being loaded (e.g., "Stories", "Events")
 */
@Composable
fun LoadingScreen(
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoadingIndicator()
    }
}

/**
 * Full-screen loading screen without title.
 */
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoadingIndicator()
    }
}
