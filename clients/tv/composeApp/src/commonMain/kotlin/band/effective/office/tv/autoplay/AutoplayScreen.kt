package band.effective.office.tv.autoplay

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import band.effective.office.tv.core.ui.Res
import band.effective.office.tv.core.ui.autoplay.Direction
import band.effective.office.tv.core.ui.autoplay.core.AutoplayFeature
import band.effective.office.tv.core.ui.autoplay.core.NavigationHandler
import band.effective.office.tv.core.ui.model.ContentCategory
import band.effective.office.tv.core.ui.no_categories_selected
import band.effective.office.tv.core.ui.press_back_to_select
import band.effective.office.tv.core.ui.screen.ErrorScreen
import band.effective.office.tv.core.ui.screen.PlaceholderScreen
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import io.github.aakira.napier.Napier
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
                val index = state.currentIndex
                val transitionKey = state.transitionKey
                val currentScreen = state.screens.getOrNull(index)
                val feature = component.featureFor(currentScreen)
                FeatureScreenContent(
                    category = currentScreen,
                    feature = feature,
                    isPlaying = state.isPlaying,
                    direction = state.direction,
                    transitionKey = transitionKey,
                    setNavigationHandler = { handler -> component.setNavigationHandler(handler) },
                    clearNavigationHandler = { handler -> component.clearNavigationHandler(handler) }
                )
            }
        }
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }
}

/**
 * Content for each feature screen.
 */
@Composable
private fun FeatureScreenContent(
    category: ContentCategory?,
    feature: AutoplayFeature?,
    isPlaying: Boolean,
    direction: Direction,
    transitionKey: Int,
    setNavigationHandler: (NavigationHandler?) -> Unit,
    clearNavigationHandler: (NavigationHandler?) -> Unit,
) {
    if (feature == null) {
        Napier.e("Feature is null for category: $category - this should not happen")
        ErrorScreen(
            description = "Feature not available for category: $category",
            onRetry = {}
        )
        return
    }

    val navigationHandler = feature.navigationHandler

    // Include transitionKey so we re-run lifecycle callbacks when looping same screen.
    DisposableEffect(category, navigationHandler, transitionKey) {
        Napier.d("Feature shown: $category, direction: $direction")
        feature.onShown(direction)
        setNavigationHandler(navigationHandler)
        onDispose {
            Napier.d("Feature hidden: $category")
            clearNavigationHandler(navigationHandler)
            feature.onHidden()
        }
    }

    feature.Content(isPlaying)
}
