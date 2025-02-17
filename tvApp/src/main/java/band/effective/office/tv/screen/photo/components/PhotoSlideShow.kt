package band.effective.office.tv.screen.photo.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.foundation.lazy.list.TvLazyListState
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import band.effective.office.tv.screen.photo.model.Photo
import okhttp3.OkHttpClient

@Composable
fun PhotoSlideShow(
    photos: List<Photo>,
    lazyListState: TvLazyListState = rememberTvLazyListState(),
    modifier: Modifier = Modifier,
    modifierForFocus: Modifier = Modifier,
    onError: (Int) -> Unit,
    unsafeOkHttpClient: OkHttpClient
) {
    TvLazyRow(
        state = lazyListState,
        modifier = modifier
            .fillMaxSize()
            .focusable()
    ) {
        itemsIndexed(photos) { photoIdx, photo ->
            PhotoUIItem(
                image = photo,
                modifier = modifierForFocus
                    .fillMaxSize(),
                onError = {
                    if (lazyListState.firstVisibleItemIndex == photoIdx) {
                        onError(photoIdx)
                    }
                },
                okHttpClient = unsafeOkHttpClient
            )
        }
    }
}