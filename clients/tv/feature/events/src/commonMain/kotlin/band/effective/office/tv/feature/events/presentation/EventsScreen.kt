package band.effective.office.tv.feature.events.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import band.effective.office.tv.core.ui.components.LoadingScreen
import band.effective.office.tv.core.ui.screen.ErrorScreen
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import band.effective.office.tv.feature.events.Res
import band.effective.office.tv.feature.events.domain.model.EventInfo
import band.effective.office.tv.feature.events.events_empty_title
import band.effective.office.tv.feature.events.events_loading_title
import band.effective.office.tv.feature.events.presentation.components.EventCard
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatformTools

@Composable
fun EventsScreen(
    component: EventsComponent,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val state by component.state.collectAsState()
    val colors = LocalTvColorsPalette.current
    val platformContext = LocalPlatformContext.current
    val imageLoader = remember(platformContext) {
        KoinPlatformTools.defaultContext().get().get<ImageLoader> { parametersOf(platformContext) }
    }

    LaunchedEffect(state.items) {
        prefetchEvents(state.items, imageLoader, platformContext)
    }

    LaunchedEffect(isPlaying) {
        component.onIntent(EventsIntent.SetPlaying(isPlaying))
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = colors.background
    ) {
        EventsContent(
            state = state,
            imageLoader = imageLoader,
            onIntent = component::onIntent
        )
    }
}

@Composable
private fun EventsContent(
    state: EventsState,
    imageLoader: ImageLoader,
    onIntent: (EventsIntent) -> Unit
) {
    when {
        state.isLoading -> LoadingScreen(
            title = stringResource(Res.string.events_loading_title)
        )

        state.error != null && !state.hasItems -> ErrorScreen(
            description = state.error,
            onRetry = { onIntent(EventsIntent.Retry) }
        )

        !state.hasItems -> EmptyEventsScreen()

        else -> state.currentItem?.let { event ->
            EventCard(
                eventInfo = event,
                imageLoader = imageLoader,
            )
        }
    }
}

@Composable
private fun EmptyEventsScreen() {
    val colors = LocalTvColorsPalette.current
    val typography = LocalTvTypography.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.events_empty_title),
            style = typography.displaySmall,
            color = colors.textPrimary
        )
    }
}

private fun prefetchEvents(
    items: List<EventInfo>,
    imageLoader: ImageLoader,
    platformContext: PlatformContext,
) {
    items
        .mapNotNull { it.photoUrl }
        .distinct()
        .take(30)
        .forEach { url ->
            imageLoader.enqueue(
                ImageRequest.Builder(platformContext)
                    .data(url)
                    .build()
            )
        }
}
