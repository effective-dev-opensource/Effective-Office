package band.effective.office.tv.screen.photo

import androidx.compose.foundation.focusable
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.foundation.lazy.list.TvLazyListState
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import band.effective.office.tv.core.ui.screen_with_controls.ScreenWithControlsTemplate
import band.effective.office.tv.core.ui.screen_with_controls.model.MenuButton
import band.effective.office.tv.core.ui.screen_with_controls.model.MenuState
import band.effective.office.tv.screen.error.ErrorScreen
import band.effective.office.tv.screen.load.LoadScreen
import band.effective.office.tv.screen.photo.components.PhotoSlideShow
import kotlinx.coroutines.flow.update
import okhttp3.OkHttpClient

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BestPhotoScreen(viewModel: PhotoViewModel = hiltViewModel()) {
    val uiState by viewModel.state.collectAsState()

    val lazyListState: TvLazyListState = rememberTvLazyListState()
    val (contentFocus, playButton) = remember { FocusRequester.createRefs() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BestPhotoEffect.ChangePlayState -> {}
                is BestPhotoEffect.ScrollToItem -> {
                    when (effect.index) {
                        uiState.photos.size -> viewModel.sendEvent(
                            BestPhotoEvent.OnRequestSwitchScreen(
                                NavigateRequests.Forward
                            )
                        )
                        -1 -> viewModel.sendEvent(
                            BestPhotoEvent.OnRequestSwitchScreen(
                                NavigateRequests.Back
                            )
                        )
                        else -> {
                            lazyListState.animateScrollToItem(effect.index)
                        }
                    }
                }
                is BestPhotoEffect.ScrollToNextItem -> {
                    if (uiState.photos.size == lazyListState.firstVisibleItemIndex + 1) {
                        viewModel.sendEvent(BestPhotoEvent.OnRequestSwitchScreen(NavigateRequests.Forward))
                    } else {
                        lazyListState.animateScrollToItem(
                            lazyListState.firstVisibleItemIndex + 1
                        )
                    }
                }
            }
        }
    }

    when {
        uiState.isError -> {
            ErrorScreen(error = uiState.error)
        }
        uiState.isLoading -> {
            LoadScreen()
        }
        else -> {
            PhotoContent(
                onClickPreviousItemButton = {
                    viewModel.sendEvent(
                        BestPhotoEvent.OnClickPreviousItem(it)
                    )
                },
                onClickNextItemButton = {
                    viewModel.sendEvent(
                        BestPhotoEvent.OnClickNextItem(it)
                    )
                },
                onClickPlayButton = { viewModel.sendEvent(BestPhotoEvent.OnClickPlayButton) },
                onError = { num ->
                    viewModel.sendEvent(
                        BestPhotoEvent.OnClickNextItem(num)
                    )
                },
                uiState = uiState,
                playButton = playButton,
                contentFocus = contentFocus,
                lazyListState = lazyListState,
                unsafeOkHttpClient = viewModel.unsafeOkHttpClient
            )
        }
    }
}

@Composable
private fun PhotoContent(
    onClickPreviousItemButton: (Int) -> Unit,
    onClickNextItemButton: (Int) -> Unit,
    onClickPlayButton: () -> Unit,
    onError: (Int) -> Unit,
    uiState: BestPhotoState,
    lazyListState: TvLazyListState,
    contentFocus: FocusRequester,
    playButton: FocusRequester,
    unsafeOkHttpClient: OkHttpClient
) {

    ScreenWithControlsTemplate(
        modifier = Modifier.onFocusChanged { focusState ->
            if (focusState.isFocused) MenuState.state.update { it.copy(selectButton = MenuButton.Nothink) }
        },
        currentListPosition = lazyListState.firstVisibleItemIndex,
        countItems = uiState.photos.size,
        isPlay = uiState.isPlay,
        contentFocus = contentFocus,
        playButton = playButton,
        content = {
            PhotoSlideShow(
                photos = uiState.photos,
                lazyListState = lazyListState,
                modifierForFocus = Modifier
                    .focusRequester(contentFocus)
                    .focusProperties {
                        down = playButton
                    }
                    .focusable(),
                onError = onError,
                unsafeOkHttpClient = unsafeOkHttpClient
            )
        },
        onClickPlayButton = onClickPlayButton,
        onClickNextItemButton = { onClickNextItemButton(lazyListState.firstVisibleItemIndex) },
        onClickPreviousItemButton = { onClickPreviousItemButton(lazyListState.firstVisibleItemIndex) },
    )
}