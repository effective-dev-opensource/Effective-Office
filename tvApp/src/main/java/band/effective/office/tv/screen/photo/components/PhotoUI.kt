package band.effective.office.tv.screen.photo.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import band.effective.office.tv.R
import band.effective.office.tv.screen.load.LoadScreen
import band.effective.office.tv.screen.photo.model.Photo
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import okhttp3.OkHttpClient


@Composable
fun PhotoUIItem(
    image: Photo,
    modifier: Modifier = Modifier,
    onError: () -> Unit,
    okHttpClient: OkHttpClient
) {
    val context = LocalContext.current

    val photoWith = LocalConfiguration.current.screenWidthDp.dp
    val photoHeight = LocalConfiguration.current.screenHeightDp.dp

    val imageLoader = ImageLoader.Builder(context)
        .okHttpClient(okHttpClient)
        .build()

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier.size(
                width = photoWith,
                height = photoHeight,
            ),
            imageLoader = imageLoader,
            model = image.photoThumb,
            loading = { LoadScreen(stringResource(id = R.string.photo_title)) },
            contentDescription = null,
            contentScale = ContentScale.Fit,
            error = {
                onError()
            }
        )
    }
}