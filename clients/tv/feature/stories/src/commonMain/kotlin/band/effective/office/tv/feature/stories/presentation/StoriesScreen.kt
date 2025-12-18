package band.effective.office.tv.feature.stories.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import band.effective.office.tv.core.ui.components.LoadingScreen
import band.effective.office.tv.core.ui.screen.ErrorScreen
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.feature.stories.domain.model.StoryDomainModel
import band.effective.office.tv.feature.stories.Res
import band.effective.office.tv.feature.stories.*
import band.effective.office.tv.feature.stories.presentation.components.DuolingoStoryContent
import band.effective.office.tv.feature.stories.presentation.components.SportStoryContent
import band.effective.office.tv.feature.stories.presentation.components.SupernovaStoryContent
import band.effective.office.tv.feature.stories.presentation.components.WarningBanner
import band.effective.office.tv.feature.stories.presentation.story.EmployeeStoryContent
import band.effective.office.tv.feature.stories.presentation.story.NoStoriesScreen
import band.effective.office.tv.feature.stories.presentation.story.StoryIndicator
import band.effective.office.tv.feature.stories.presentation.story.storyBackground
import coil3.compose.LocalPlatformContext
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.PlatformContext
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatformTools

@Composable
fun StoriesScreen(
    component: StoriesComponent,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val state by component.state.collectAsState()
    val progress by component.progress.collectAsState()
    val colors = LocalTvColorsPalette.current
    val sizes = LocalTvSizes.current
    val platformContext = LocalPlatformContext.current
    val imageLoader = androidx.compose.runtime.remember(platformContext) {
        KoinPlatformTools.defaultContext().get().get<ImageLoader> { parametersOf(platformContext) }
    }
    val currentStory = state.currentItem
    val currentIndex = state.currentIndex

    // Prefetch all story images into cache so subsequent screens load instantly
    LaunchedEffect(state.items) {
        prefetchImages(state.items, imageLoader, platformContext)
    }

    LaunchedEffect(isPlaying) {
        component.onIntent(StoriesIntent.SetPlaying(isPlaying))
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = storyBackground(currentStory, colors)
    ) {
        when {
            state.isLoading -> LoadingScreen(
                title = stringResource(Res.string.stories_loading_title)
            )
            state.error != null && !state.hasItems -> ErrorScreen(
                description = state.error.orEmpty(),
                onRetry = { component.onIntent(StoriesIntent.Retry) }
            )
            !state.hasItems -> NoStoriesScreen()
            currentStory != null -> {
                Box(Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(sizes.gapLarge),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        StoryIndicator(
                            countStories = state.items.size,
                            currentStoryIndex = currentIndex,
                            progress = if (state.hasItems) progress else 0f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = sizes.gapLarge, vertical = sizes.gapLarge),
                            activeColor = colors.storyActiveIndicator,
                            inactiveColor = colors.storyIndicator
                        )

                        when (currentStory) {
                            is StoryDomainModel.EmployeeStory -> EmployeeStoryContent(
                                story = currentStory,
                                imageLoader = imageLoader
                            )

                            is StoryDomainModel.DuolingoStory -> DuolingoStoryContent(
                                story = currentStory,
                                imageLoader = imageLoader
                            )

                            is StoryDomainModel.SportStory -> SportStoryContent(
                                story = currentStory,
                                imageLoader = imageLoader
                            )

                            is StoryDomainModel.SupernovaStory -> SupernovaStoryContent(
                                story = currentStory,
                                imageLoader = imageLoader
                            )
                        }
                    }

                    WarningBanner(
                        warnings = state.warnings,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(
                                horizontal = sizes.gapLarge,
                                vertical = sizes.gapMedium
                            )
                    )
                }
            }
        }
    }
}

private fun prefetchImages(
    items: List<StoryDomainModel>,
    imageLoader: ImageLoader,
    platformContext: PlatformContext
) {
    val urls = items.flatMap { story ->
        when (story) {
            is StoryDomainModel.EmployeeStory -> listOf(story.photoUrl)
            is StoryDomainModel.DuolingoStory -> story.users.mapNotNull { it.photo }
            is StoryDomainModel.SportStory -> story.users.mapNotNull { it.photo }
            is StoryDomainModel.SupernovaStory -> story.users.mapNotNull { it.photo }
        }
    }
        .filter { !it.isNullOrBlank() }
        .toSet() // deduplicate
        .take(60) // limit prefetch batch

    urls.forEach { url ->
        imageLoader.enqueue(
            ImageRequest.Builder(platformContext)
                .data(url)
                .build()
        )
    }
}
