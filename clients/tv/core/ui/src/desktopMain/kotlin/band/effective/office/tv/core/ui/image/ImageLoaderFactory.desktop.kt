package band.effective.office.tv.core.ui.image

import coil3.PlatformContext
import okio.Path
import okio.Path.Companion.toPath

actual fun getCacheDirectory(context: PlatformContext): Path =
    (System.getProperty("java.io.tmpdir") + "/image_cache").toPath()

