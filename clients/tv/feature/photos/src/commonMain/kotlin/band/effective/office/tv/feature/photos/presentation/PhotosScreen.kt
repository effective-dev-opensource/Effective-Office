package band.effective.office.tv.feature.photos.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import band.effective.office.tv.core.ui.components.LoadingScreen
import band.effective.office.tv.core.ui.screen.ErrorScreen
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.feature.photos.Res
import band.effective.office.tv.feature.photos.photos_loading_title
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatformTools
import band.effective.office.tv.feature.photos.presentation.components.EmptyPhotosScreen
import band.effective.office.tv.feature.photos.presentation.components.PhotoSlideshow
import band.effective.office.tv.feature.photos.presentation.components.validateAndRemoveFailedPhotos

/**
 * Main entry point for the Photos feature screen.
 * Observes the component state and delegates UI rendering to specialized composables.
 */
@Composable
fun PhotosScreen(
    component: PhotosComponent,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsState()
    val platformContext = LocalPlatformContext.current
    val imageLoader = remember(platformContext) {
        KoinPlatformTools.defaultContext().get().get<ImageLoader> { parametersOf(platformContext) }
    }

    // Removes obviously broken links before slideshow starts
    LaunchedEffect(state.items) {
        if (state.items.isEmpty()) return@LaunchedEffect

        validateAndRemoveFailedPhotos(
            photos = state.items,
            imageLoader = imageLoader,
            platformContext = platformContext,
            onRemove = { url -> component.onIntent(PhotosIntent.RemoveFailedPhoto(url)) }
        )
    }
    // Sync external play/pause control with the component
    LaunchedEffect(isPlaying) {
        component.onIntent(PhotosIntent.SetPlaying(isPlaying))
    }

    Surface(modifier = modifier.fillMaxSize(), color = LocalTvColorsPalette.current.background) {
        PhotosContent(state = state, imageLoader = imageLoader, onIntent = component::onIntent)
    }
}

/**
 * Central composable that routes to the appropriate screen based on current PhotosState.
 */
@Composable
private fun PhotosContent(
    state: PhotosState,
    imageLoader: ImageLoader,
    onIntent: (PhotosIntent) -> Unit
) {
    when {
        state.isLoading -> LoadingScreen(title = stringResource(Res.string.photos_loading_title))
        state.error != null && state.items.isEmpty() -> ErrorScreen(
            description = state.error,
            onRetry = { onIntent(PhotosIntent.Retry) }
        )
        state.items.isEmpty() -> EmptyPhotosScreen()
        else -> PhotoSlideshow(
            photos = state.items,
            currentIndex = state.currentIndex,
            imageLoader = imageLoader,
            onRemoveFailed = { url -> onIntent(PhotosIntent.RemoveFailedPhoto(url)) }
        )
    }
}

