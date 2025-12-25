package band.effective.office.tv.feature.photos.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.size.Size
import io.github.aakira.napier.Napier

@Composable
fun PhotoSlideshow(
    photos: List<band.effective.office.tv.feature.photos.domain.model.Photo>,
    currentIndex: Int,
    imageLoader: ImageLoader,
    onRemoveFailed: (String) -> Unit
) {
    if (photos.isEmpty()) return

    val currentPhoto = photos.getOrNull(currentIndex) ?: photos.first()

    val platformContext = LocalPlatformContext.current

    LaunchedEffect(currentIndex) {
        prefetchNextPhotos(
            photos = photos,
            currentIndex = currentIndex,
            imageLoader = imageLoader,
            platformContext = platformContext,
        )
    }

    AnimatedContent(
        targetState = currentPhoto,
        transitionSpec = {
            val forward = isTransitionForward(
                photos = photos,
                previousPhoto = initialState,
                nextPhoto = targetState
            )
            (slideInHorizontally { if (forward) it else -it } + fadeIn()) togetherWith
                    (slideOutHorizontally { if (forward) -it else it } + fadeOut())
        },
        label = "photo_slideshow"
    ) { photo ->
        AsyncImage(
            model = photo.url,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            imageLoader = imageLoader,
            contentScale = ContentScale.Fit,
            onState = { state ->
                if (state is AsyncImagePainter.State.Error) {
                    Napier.w("Broken image during display - removing: ${photo.url}")
                    onRemoveFailed(photo.url)
                }
            }
        )
    }
}

private fun isTransitionForward(
    photos: List<band.effective.office.tv.feature.photos.domain.model.Photo>,
    previousPhoto: band.effective.office.tv.feature.photos.domain.model.Photo?,
    nextPhoto: band.effective.office.tv.feature.photos.domain.model.Photo
): Boolean {
    if (previousPhoto == null) return true
    val prevIndex = photos.indexOf(previousPhoto)
    val nextIndex = photos.indexOf(nextPhoto)
    return nextIndex == -1 || prevIndex == -1 || nextIndex > prevIndex
}

private fun prefetchNextPhotos(
    photos: List<band.effective.office.tv.feature.photos.domain.model.Photo>,
    currentIndex: Int,
    imageLoader: ImageLoader,
    platformContext: PlatformContext
) {
    for (i in 1..5) {
        val nextIndex = (currentIndex + i) % photos.size
        val url = photos[nextIndex].url

        imageLoader.enqueue(
            ImageRequest.Builder(platformContext)
                .data(url)
                .size(Size.ORIGINAL)
                .build()
        )
    }
}
