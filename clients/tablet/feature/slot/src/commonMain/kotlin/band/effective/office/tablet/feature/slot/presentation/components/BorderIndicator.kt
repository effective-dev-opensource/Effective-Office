package band.effective.office.tablet.feature.slot.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.theme.deleteProgressColor
import kotlinx.coroutines.delay

//https://stackoverflow.com/questions/75745905/rectangle-border-progress-bar
@Composable
fun BorderIndicator(
    modifier: Modifier = Modifier.fillMaxSize(),
    startDurationInSeconds: Int = 10,
    stokeWidth: Dp,
    initialProgress: Float = 100f,
    onDispose: () -> Unit
) {
    var targetValue by remember {
        mutableStateOf(initialProgress)
    }
    // This is the progress path which wis changed using path measure
    val pathWithProgress by remember { mutableStateOf(Path()) }

    // using path
    val pathMeasure by remember { mutableStateOf(PathMeasure()) }

    val path = remember { Path() }

    val progress by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(startDurationInSeconds * 1000, easing = LinearEasing), label = ""
    )

    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = modifier) {
            if (path.isEmpty) {
                path.addRoundRect(
                    RoundRect(
                        Rect(offset = Offset.Zero, size),
                        cornerRadius = CornerRadius(100.dp.toPx(), 100.dp.toPx())
                    )
                )
            }
            pathWithProgress.reset()
            pathMeasure.setPath(path, forceClosed = false)
            pathMeasure.getSegment(
                startDistance = 0f,
                stopDistance = pathMeasure.length * progress / 100f,
                destination = pathWithProgress,
                startWithMoveTo = true
            )

            drawPath(
                path = path,
                style = Stroke(stokeWidth.toPx()),
                color = Color.Gray
            )

            drawPath(
                path = pathWithProgress,
                style = Stroke(stokeWidth.toPx()),
                color = deleteProgressColor
            )
        }
    }
    LaunchedEffect(Unit) {
        targetValue = 0f
        // Calculate remaining duration based on initial progress
        val remainingDuration = (startDurationInSeconds * 1000L * initialProgress / 100f).toLong()
        delay(remainingDuration)
        onDispose()
    }
}
