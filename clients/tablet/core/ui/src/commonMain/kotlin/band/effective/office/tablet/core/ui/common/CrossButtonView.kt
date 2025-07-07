package band.effective.office.tablet.core.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette

/**
 * A button with a cross icon, typically used for closing dialogs or screens
 *
 * @param modifier Modifier to be applied to the button
 * @param onDismissRequest Callback to be invoked when the button is clicked
 */
@Composable
fun CrossButtonView(modifier: Modifier, onDismissRequest: () -> Unit) {
    // Get the color outside the Canvas
    val iconColor = LocalCustomColorsPalette.current.secondaryTextAndIcon

    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        IconButton(
            onClick = { onDismissRequest() },
            modifier = Modifier.size(40.dp)
        ) {
            // Note: In the original implementation, this used a vector resource
            // For now, we'll draw a simple X using Canvas
            // In a real implementation, you would use a proper icon resource
            CrossIcon(
                modifier = Modifier.size(25.dp),
                color = iconColor
            )
        }
    }
}

/**
 * A simple cross (X) icon drawn using Canvas
 *
 * @param modifier Modifier to be applied to the canvas
 * @param color Color of the cross
 */
@Composable
private fun CrossIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.5f

        // Draw the X
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(0f, size.height),
            end = Offset(size.width, 0f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
