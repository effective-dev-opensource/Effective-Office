package band.effective.office.tv.feature.events.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp

@Composable
internal expect fun rememberQrPainter(
    content: String,
    size: Dp,
    color: Color = Color.White,
    backgroundColor: Color = Color.Black
): Painter
