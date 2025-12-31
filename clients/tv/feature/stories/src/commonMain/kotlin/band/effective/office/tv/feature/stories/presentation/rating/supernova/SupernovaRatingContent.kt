package band.effective.office.tv.feature.stories.presentation.rating.supernova

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.feature.stories.Res
import band.effective.office.tv.feature.stories.*
import band.effective.office.tv.feature.stories.domain.model.supernova.SupernovaScore
import band.effective.office.tv.feature.stories.presentation.rating.common.TopRatingScreen
import coil3.ImageLoader
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun SupernovaRatingContent(
    users: List<SupernovaScore>,
    imageLoader: ImageLoader,
) {
    val colors = LocalTvColorsPalette.current

    TopRatingScreen(
        users = users,
        backgroundColor = colors.supernova,
        title = stringResource(Res.string.supernova_title),
        logo = painterResource(Res.drawable.supernova)
    ) { user, place ->
        SupernovaItem(
            modifier = Modifier.fillMaxWidth(),
            user = user,
            place = place,
            imageLoader = imageLoader
        )
    }
}

