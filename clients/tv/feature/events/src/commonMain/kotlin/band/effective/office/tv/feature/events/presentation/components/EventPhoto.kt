package band.effective.office.tv.feature.events.presentation.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import band.effective.office.tv.core.ui.Res as CoreRes
import band.effective.office.tv.core.ui.icon_event_orange
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.feature.events.Res
import band.effective.office.tv.feature.events.domain.model.EventInfo
import band.effective.office.tv.feature.events.events_content_description
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EventPhoto(
    eventInfo: EventInfo,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier
) {
    val context = LocalPlatformContext.current
    val sizes = LocalTvSizes.current
    
    BoxWithConstraints(
        modifier = modifier
            .fillMaxHeight()
            .padding(start = sizes.gapMedium)
    ) {
        val request = remember(eventInfo.photoUrl) {
            ImageRequest.Builder(context)
                .data(eventInfo.photoUrl)
                .size(Size.ORIGINAL)
                .crossfade(true)
                .build()
        }

        AsyncImage(
            model = request,
            imageLoader = imageLoader,
            contentDescription = stringResource(Res.string.events_content_description),
            modifier = Modifier.fillMaxHeight(),
            contentScale = ContentScale.Crop,
            error = painterResource(CoreRes.drawable.icon_event_orange),
            fallback = painterResource(CoreRes.drawable.icon_event_orange),
        )
    }
}
