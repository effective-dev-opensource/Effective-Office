package band.effective.office.tv.autoplay

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import band.effective.office.tv.core.ui.model.ContentCategory
import band.effective.office.tv.core.ui.screen.ErrorScreen
import band.effective.office.tv.core.ui.screen.PlaceholderScreen
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.Res
import band.effective.office.tv.core.ui.no_categories_selected
import band.effective.office.tv.core.ui.press_back_to_select
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

/**
 * Autoplay screen - slideshow container for feature screens.
 * Cycles through selected categories.
 */
@Composable
fun AutoplayScreen(
    component: AutoplayComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.collectAsState()
    val colors = LocalTvColorsPalette.current
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.DirectionLeft -> {
                            component.onIntent(AutoplayIntent.PreviousScreen)
                            true
                        }
                        Key.DirectionRight -> {
                            component.onIntent(AutoplayIntent.NextScreen)
                            true
                        }
                        Key.Escape, Key.Back -> {
                            component.onIntent(AutoplayIntent.Back)
                            true
                        }
                        Key.Spacebar, Key.DirectionCenter -> {
                            component.onIntent(AutoplayIntent.TogglePause)
                            true
                        }
                        else -> false
                    }
                } else false
            }
    ) {
        when {
            state.error != null -> {
                ErrorScreen(
                    description = state.error,
                    onRetry = { component.onIntent(AutoplayIntent.Retry) }
                )
            }
            state.screens.isEmpty() -> {
                PlaceholderScreen(
                    title = stringResource(Res.string.no_categories_selected),
                    subtitle = stringResource(Res.string.press_back_to_select)
                )
            }
            else -> {
                // Use transitionKey to ensure animation triggers even when cycling back to same index
                AnimatedContent(
                    targetState = state.currentIndex to state.transitionKey,
                    transitionSpec = {
                        val direction = if (state.direction == Direction.FORWARD) 1 else -1
                        (slideInHorizontally { fullWidth -> direction * fullWidth } + fadeIn())
                            .togetherWith(slideOutHorizontally { fullWidth -> -direction * fullWidth } + fadeOut())
                    },
                    label = "screen_transition"
                ) { (index, _) ->
                    val currentScreen = state.screens.getOrNull(index)
                    FeatureScreenContent(
                        category = currentScreen,
                        isPlaying = state.isPlaying,
                        screenIndex = index,
                        totalScreens = state.screens.size,
                        onFinished = { component.onScreenFinished() }
                    )
                }
            }
        }
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }
}

/**
 * Content for each feature screen.
 * This will be replaced with actual feature implementations later.
 */
@Composable
private fun FeatureScreenContent(
    category: ContentCategory?,
    isPlaying: Boolean,
    screenIndex: Int,
    totalScreens: Int,
    onFinished: () -> Unit,
) {
    // TODO: Replace with actual feature screens
    // Each feature screen should:
    // 1. Load its data (photos, events, stories)
    // 2. Show items one by one with auto-advance
    // 3. Call onFinished() when all items are shown
    
    val (title, subtitle) = when (category) {
        ContentCategory.STORIES -> "Stories" to "Stories"
        ContentCategory.PHOTOS -> "Photos" to "Team photos"
        ContentCategory.EVENTS -> "Events" to "Upcoming events"
        null -> "Unknown" to "Unknown category"
    }

    val statusText = "Screen ${screenIndex + 1} of $totalScreens"

    LaunchedEffect(category, isPlaying) {
        if (isPlaying) {
            delay(PLACEHOLDER_SCREEN_DURATION_MS)
            onFinished()
        }
    }

    PlaceholderScreen(
        title = title,
        subtitle = "$subtitle\n\n$statusText\n\n${if (isPlaying) "Playing..." else "Paused"}"
    )
}

private const val PLACEHOLDER_SCREEN_DURATION_MS = 5_000L // 5 seconds for demo
