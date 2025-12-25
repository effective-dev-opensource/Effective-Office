package band.effective.office.tv.feature.photos.presentation.components

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.request.ImageRequest
import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.awaitAll
import band.effective.office.tv.feature.photos.domain.model.Photo

suspend fun validateAndRemoveFailedPhotos(
    photos: List<Photo>,
    imageLoader: ImageLoader,
    platformContext: PlatformContext,
    onRemove: (String) -> Unit
) {
    val results = coroutineScope {
        photos.map { photo ->
            async(Dispatchers.IO) {
                val request = ImageRequest.Builder(platformContext)
                    .data(photo.url)
                    .size(1, 1)
                    .build()

                val result = runCatching { imageLoader.execute(request) }
                val isValid = result.isSuccess && result.getOrNull()?.image != null
                photo to isValid
            }
        }.awaitAll()
    }

    results.filter { !it.second }.forEach { (photo, _) ->
        onRemove(photo.url)
    }

    Napier.d("Validation completed. All corrupted images removed.")
}
