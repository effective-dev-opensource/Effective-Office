package band.effective.office.tv.feature.events.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import org.jetbrains.skia.Surface
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ImageInfo

@Composable
internal actual fun rememberQrPainter(
    content: String,
    size: Dp,
    color: Color,
    backgroundColor: Color
): Painter {
    val sizePx = with(LocalDensity.current) { size.roundToPx() }
    return remember(content, sizePx, color, backgroundColor) {
        generateQrPainter(content, sizePx, color, backgroundColor)
    }
}

private fun generateQrPainter(
    content: String,
    sizePx: Int,
    color: Color,
    backgroundColor: Color
): Painter {
    val writer = QRCodeWriter()
    val matrix = runCatching {
        writer.encode(
            content,
            BarcodeFormat.QR_CODE,
            sizePx,
            sizePx,
            mapOf(EncodeHintType.MARGIN to 0)
        )
    }.getOrNull()

    val surface = Surface.makeRaster(ImageInfo.makeN32(sizePx, sizePx, ColorAlphaType.PREMUL))
    val canvas = surface.canvas
    canvas.clear(backgroundColor.toArgb())

    val paint = Paint().apply { this.color = color.toArgb() }
    for (x in 0 until sizePx) {
        for (y in 0 until sizePx) {
            if (matrix?.get(x, y) == true) {
                canvas.drawRect(Rect.makeXYWH(x.toFloat(), y.toFloat(), 1f, 1f), paint)
            }
        }
    }

    val image = surface.makeImageSnapshot()
    return BitmapPainter(image.toComposeImageBitmap())
}
