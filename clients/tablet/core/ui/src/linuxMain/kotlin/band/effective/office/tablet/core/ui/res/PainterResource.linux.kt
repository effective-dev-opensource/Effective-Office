package band.effective.office.tablet.core.ui.res

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.res.vectorxml.parse
import band.effective.office.tablet.core.ui.res.vectorxml.toImageVector
import io.github.aakira.napier.Napier
import kotlin.coroutines.cancellation.CancellationException
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.getDrawableResourceBytes
import org.jetbrains.compose.resources.rememberResourceEnvironment
import org.jetbrains.skia.Data
import org.jetbrains.skia.svg.SVGDOM

private const val PLACEHOLDER_SIZE_DP = 24
private const val SVG_TAG = "<svg"
private const val RESOURCE_TAG = "Resources"

// Fork defect: the fork's own loader feeds every drawable to SVGDOM, and every icon this project
// ships is vector XML. See "Fork defects" in clients/tablet/core/ui/README.md.
/**
 * Resolves the drawable bytes here and picks the decoder by their signature. Reading is suspending,
 * so a transparent placeholder holds the slot until it finishes.
 */
@Composable
actual fun painterResource(resource: DrawableResource): Painter {
    val environment = rememberResourceEnvironment()
    val density = LocalDensity.current
    val placeholder = remember { Decoded.Vector(placeholderVector()) }
    val decoded by produceState<Decoded>(placeholder, resource, environment, density) {
        value = decodeOrNull(resource) {
            decodeDrawable(getDrawableResourceBytes(environment, resource), density)
        } ?: placeholder
    }
    return when (val current = decoded) {
        is Decoded.Vector -> rememberVectorPainter(current.image)
        is Decoded.Raster -> remember(current) { BitmapPainter(current.bitmap) }
        is Decoded.Svg -> current.painter
    }
}

/** See [painterResource]. */
@Composable
actual fun vectorResource(resource: DrawableResource): ImageVector {
    val environment = rememberResourceEnvironment()
    val density = LocalDensity.current
    val placeholder = remember { placeholderVector() }
    val image by produceState(placeholder, resource, environment, density) {
        value = decodeOrNull(resource) {
            parse(getDrawableResourceBytes(environment, resource).decodeToString()).toImageVector(density)
        } ?: placeholder
    }
    return image
}

/** A drawable that cannot be decoded leaves a hole in the screen, so say which one it was. */
private inline fun <T> decodeOrNull(resource: DrawableResource, decode: () -> T): T? = try {
    decode()
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (failure: Throwable) {
    Napier.e(throwable = failure, tag = RESOURCE_TAG) { "decoding $resource failed" }
    null
}

private sealed interface Decoded {
    data class Vector(val image: ImageVector) : Decoded
    data class Raster(val bitmap: ImageBitmap) : Decoded
    data class Svg(val painter: Painter) : Decoded
}

private fun placeholderVector(): ImageVector = ImageVector.Builder(
    defaultWidth = PLACEHOLDER_SIZE_DP.dp,
    defaultHeight = PLACEHOLDER_SIZE_DP.dp,
    viewportWidth = PLACEHOLDER_SIZE_DP.toFloat(),
    viewportHeight = PLACEHOLDER_SIZE_DP.toFloat(),
).build()

private fun decodeDrawable(bytes: ByteArray, density: Density): Decoded {
    if (bytes.isRasterImage()) return Decoded.Raster(bytes.decodeToImageBitmap())
    val markup = bytes.decodeToString()
    return if (markup.contains(SVG_TAG)) {
        Decoded.Svg(bytes.toSvgPainter())
    } else {
        Decoded.Vector(parse(markup).toImageVector(density))
    }
}

private val PNG_MAGIC = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47)
private val JPEG_MAGIC = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())
private val BMP_MAGIC = byteArrayOf(0x42, 0x4D)
private val RIFF_MAGIC = byteArrayOf(0x52, 0x49, 0x46, 0x46)
private val WEBP_MAGIC = byteArrayOf(0x57, 0x45, 0x42, 0x50)
private const val WEBP_MAGIC_OFFSET = 8

private fun ByteArray.startsWith(magic: ByteArray, offset: Int = 0): Boolean =
    size >= offset + magic.size && magic.indices.all { this[offset + it] == magic[it] }

private fun ByteArray.isRasterImage(): Boolean =
    startsWith(PNG_MAGIC) ||
        startsWith(JPEG_MAGIC) ||
        startsWith(BMP_MAGIC) ||
        (startsWith(RIFF_MAGIC) && startsWith(WEBP_MAGIC, WEBP_MAGIC_OFFSET))

/** Colors are baked into an SVG, so `Icon(tint = …)` does not reach this painter. */
private fun ByteArray.toSvgPainter(): Painter {
    val dom = SVGDOM(Data.makeFromBytes(this))
    val root = dom.root ?: return EmptyPainter
    return object : Painter() {
        override val intrinsicSize: Size
            get() = Size(root.width.value, root.height.value)

        override fun DrawScope.onDraw() {
            val scale = minOf(size.width / root.width.value, size.height / root.height.value)
            val offsetX = (size.width - root.width.value * scale) / 2
            val offsetY = (size.height - root.height.value * scale) / 2
            drawIntoCanvas { canvas ->
                canvas.save()
                canvas.translate(offsetX, offsetY)
                canvas.scale(scale, scale)
                dom.render(canvas.nativeCanvas)
                canvas.restore()
            }
        }
    }
}

private val EmptyPainter: Painter = object : Painter() {
    override val intrinsicSize: Size get() = Size.Unspecified
    override fun DrawScope.onDraw() = Unit
}
