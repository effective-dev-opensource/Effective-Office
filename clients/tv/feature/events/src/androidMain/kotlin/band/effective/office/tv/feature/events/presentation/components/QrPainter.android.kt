package band.effective.office.tv.feature.events.presentation.components

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal actual fun rememberQrPainter(
    content: String,
    size: Dp,
    color: Color,
    backgroundColor: Color
): Painter {
    val sizePx = with(LocalDensity.current) { size.roundToPx() }

    var bitmap by remember(content, sizePx) { mutableStateOf<Bitmap?>(null) }

    val colorArgb = color.toArgb()
    val backgroundColorArgb = backgroundColor.toArgb()

    LaunchedEffect(content, sizePx, colorArgb, backgroundColorArgb) {
        bitmap = withContext(Dispatchers.IO) {
            val qrCodeWriter = QRCodeWriter()
            val hints = mutableMapOf<EncodeHintType, Any?>(EncodeHintType.MARGIN to 0)

            val matrix = try {
                qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx, hints)
            } catch (_: WriterException) {
                null
            }

            val width = matrix?.width ?: sizePx
            val height = matrix?.height ?: sizePx
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        val set = matrix?.get(x, y) ?: false
                        setPixel(x, y, if (set) colorArgb else backgroundColorArgb)
                    }
                }
            }
        }
    }

    return remember(content, sizePx, bitmap) {
        BitmapPainter(
            (bitmap ?: Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
                .apply { eraseColor(colorArgb) }).asImageBitmap()
        )
    }
}
