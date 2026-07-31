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
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.getDrawableResourceBytes
import org.jetbrains.compose.resources.rememberResourceEnvironment
import org.jetbrains.skia.Data
import org.jetbrains.skia.svg.SVGDOM
import band.effective.office.tablet.core.ui.res.vectorxml.parse
import band.effective.office.tablet.core.ui.res.vectorxml.toImageVector

/**
 * Aurora actual for the [painterResource] polyfill.
 *
 * The fork's `components-resources` renders SVG only and mangles Android vector XML, so we bypass it
 * entirely: resolve the drawable's raw bytes ourselves — `getDrawableResourceBytes` (public) picks the
 * right resource item from [resource] for the current environment — and dispatch by **content
 * signature** (the byte signature, so no file path/extension is needed at the call site):
 *  - PNG / JPEG / WebP / BMP magic bytes -> [BitmapPainter] (raster);
 *  - `<svg …>` -> Skia `SVGDOM` painter;
 *  - otherwise Android vector XML -> an `ImageVector` (see the `vectorxml` package), which keeps
 *    `Icon(tint = …)` and intrinsic dp sizes.
 *
 * Reading is async (a suspend resource read); a transparent 24dp placeholder holds the slot for the
 * ~1 frame before decoding finishes.
 */
@Composable
actual fun painterResource(resource: DrawableResource): Painter {
    val environment = rememberResourceEnvironment()
    val density = LocalDensity.current
    val placeholder = remember { Decoded.Vector(placeholderVector()) }
    val decoded by produceState<Decoded>(placeholder, resource, environment, density) {
        value = runCatching {
            decodeDrawable(getDrawableResourceBytes(environment, resource), density)
        }.getOrDefault(placeholder)
    }
    return when (val current = decoded) {
        is Decoded.Vector -> rememberVectorPainter(current.image)
        is Decoded.Raster -> remember(current) { BitmapPainter(current.bitmap) }
        is Decoded.Svg -> current.painter
    }
}

/** Decoded drawable payload: a vector (from XML), a raster bitmap, or an SVG painter. */
private sealed interface Decoded {
    data class Vector(val image: ImageVector) : Decoded
    data class Raster(val bitmap: ImageBitmap) : Decoded
    data class Svg(val painter: Painter) : Decoded
}

/**
 * Aurora actual for the [vectorResource] polyfill — same reason as [painterResource], but the fork's
 * own `vectorResource` is what actually crashes: it feeds the XML to Skia's SVGDOM and dies with
 * "Can't wrap nullptr" on an Android Vector Drawable.
 */
@Composable
actual fun vectorResource(resource: DrawableResource): ImageVector {
    val environment = rememberResourceEnvironment()
    val density = LocalDensity.current
    val placeholder = remember { placeholderVector() }
    val image by produceState(placeholder, resource, environment, density) {
        value = runCatching {
            parse(getDrawableResourceBytes(environment, resource).decodeToString()).toImageVector(density)
        }.getOrDefault(placeholder)
    }
    return image
}

private fun placeholderVector(): ImageVector = ImageVector.Builder(
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).build()

/** Picks the painter kind from the byte signature (no path/extension needed). */
private fun decodeDrawable(bytes: ByteArray, density: Density): Decoded = when {
    bytes.isRasterImage() -> Decoded.Raster(bytes.decodeToImageBitmap())
    bytes.looksLikeSvg() -> Decoded.Svg(bytes.toSvgPainter())
    else -> Decoded.Vector(parse(bytes.decodeToString()).toImageVector(density))
}

/** Detects common raster formats by magic bytes: PNG, JPEG, WebP (RIFF…WEBP), BMP. */
private fun ByteArray.isRasterImage(): Boolean {
    fun u(i: Int): Int = if (i < size) this[i].toInt() and 0xFF else -1
    // PNG: 89 50 4E 47
    if (u(0) == 0x89 && u(1) == 0x50 && u(2) == 0x4E && u(3) == 0x47) return true
    // JPEG: FF D8 FF
    if (u(0) == 0xFF && u(1) == 0xD8 && u(2) == 0xFF) return true
    // BMP: 42 4D ("BM")
    if (u(0) == 0x42 && u(1) == 0x4D) return true
    // WebP: "RIFF" .... "WEBP"
    if (u(0) == 0x52 && u(1) == 0x49 && u(2) == 0x46 && u(3) == 0x46 &&
        u(8) == 0x57 && u(9) == 0x45 && u(10) == 0x42 && u(11) == 0x50
    ) {
        return true
    }
    return false
}

/**
 * True for an SVG document. An Android Vector Drawable has a `<vector>` root and never contains an
 * `<svg` tag, so this cheap substring check disambiguates the two XML dialects.
 */
private fun ByteArray.looksLikeSvg(): Boolean = decodeToString().contains("<svg")

/**
 * Renders SVG bytes through Skia's SVGDOM, scaled/centred into the draw bounds (mirrors the fork's
 * own SVG painter). Note: colors are baked in the SVG — `Icon(tint = …)` does not apply.
 */
private fun ByteArray.toSvgPainter(): Painter {
    val dom = SVGDOM(Data.makeFromBytes(this))
    val root = dom.root ?: return EmptyPainter
    return object : Painter() {
        override val intrinsicSize: Size
            get() = Size(root.width.value, root.height.value)

        override fun DrawScope.onDraw() {
            val scaleX = size.width / root.width.value
            val scaleY = size.height / root.height.value
            val scale = minOf(scaleX, scaleY)
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
