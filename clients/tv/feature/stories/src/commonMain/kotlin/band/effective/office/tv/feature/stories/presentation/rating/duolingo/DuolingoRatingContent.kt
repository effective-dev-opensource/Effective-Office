package band.effective.office.tv.feature.stories.presentation.rating.duolingo

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.feature.stories.Res
import band.effective.office.tv.feature.stories.*
import band.effective.office.tv.feature.stories.domain.model.DuolingoKey
import band.effective.office.tv.feature.stories.domain.model.DuolingoUser
import band.effective.office.tv.feature.stories.presentation.rating.common.TopRatingScreen
import coil3.ImageLoader
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DuolingoRatingContent(
    key: DuolingoKey,
    users: List<DuolingoUser>,
    imageLoader: ImageLoader,
) {
    val colors = LocalTvColorsPalette.current

    TopRatingScreen(
        users = users,
        backgroundColor = colors.duolingo,
        title = stringResource(Res.string.duolingo_title),
        logo = painterResource(Res.drawable.duolingo_logo),
        logoModifier = Modifier.clip(CircleShape)
    ) { user, place ->
        val indicator = formatDuolingoIndicator(key, user)
        DuolingoItem(
            user = user,
            indicator = indicator,
            place = place,
            key = key,
            imageLoader = imageLoader
        )
    }
}

