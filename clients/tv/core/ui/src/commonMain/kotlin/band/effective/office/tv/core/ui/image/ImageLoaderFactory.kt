package band.effective.office.tv.core.ui.image

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade

/**
 * Shared ImageLoader for TV app with memory+disk cache.
 */
fun createTvImageLoader(context: PlatformContext): ImageLoader =
    ImageLoader.Builder(context)
        .crossfade(100)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, percent = 0.30)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(getCacheDirectory(context))
                .maxSizeBytes(30L * 1024 * 1024) // 30 MB
                .build()
        }
        .build()

/**
 * Platform-specific cache directory.
 */
expect fun getCacheDirectory(context: PlatformContext): okio.Path

