package band.effective.office.tv.feature.menu.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvShapes
import kotlin.time.Clock
import kotlinx.coroutines.delay

private const val AUTO_CLICK_DELAY_MS = 15000L // 15 seconds
/** Frame delay for progress updates */
private const val PROGRESS_FRAME_DELAY_MS = 16L

/** Alpha for the lightening overlay that shows progress */
private const val OVERLAY_ALPHA = 0.3f

/**
 * Orange button with progress overlay that moves left-to-right.
 */
@Composable
fun ProgressButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    autoClickEnabled: Boolean = true,
    autoClickDelayMs: Long = AUTO_CLICK_DELAY_MS,
    focusRequester: FocusRequester? = null,
    content: @Composable (isFocused: Boolean) -> Unit = {}
) {
    val colors = LocalTvColorsPalette.current
    val shapes = LocalTvShapes.current
    var isFocused by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    // Button is always orange when focused, gray otherwise
    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused) colors.primary else colors.secondary,
        label = "progress_button_background"
    )

    // Auto-click timer: progress goes from 0 to 1 smoothly over autoClickDelayMs
    LaunchedEffect(isFocused, autoClickEnabled, autoClickDelayMs) {
        if (isFocused && autoClickEnabled) {
            progress = 0f
            val startTime = Clock.System.now().toEpochMilliseconds()

            while (true) {
                val elapsed = Clock.System.now().toEpochMilliseconds() - startTime
                progress = (elapsed.toFloat() / autoClickDelayMs).coerceIn(0f, 1f)

                if (progress >= 1f) {
                    onClick()
                    break
                }

                delay(PROGRESS_FRAME_DELAY_MS)
            }
        } else {
            progress = 0f
        }
    }

    Box(
        modifier = modifier
            .clip(shapes.large)
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .onFocusChanged { isFocused = it.isFocused }
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown &&
                    (event.key == Key.Enter || event.key == Key.DirectionCenter)
                ) {
                    onClick()
                    true
                } else false
            }
            .clickable { onClick() }
            .focusable()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        // Content (icon + text)
        content(isFocused)

        // Light overlay on the LEFT that grows as progress increases
        if (isFocused && autoClickEnabled && progress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = progress)
                    .align(Alignment.CenterStart)
                    .background(Color.White.copy(alpha = OVERLAY_ALPHA))
            )
        }
    }
}
