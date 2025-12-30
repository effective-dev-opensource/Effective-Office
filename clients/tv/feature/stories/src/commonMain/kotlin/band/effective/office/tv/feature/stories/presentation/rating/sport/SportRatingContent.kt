package band.effective.office.tv.feature.stories.presentation.rating.sport

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.feature.stories.Res
import band.effective.office.tv.feature.stories.*
import band.effective.office.tv.feature.stories.domain.model.sport.ClockifyUser
import band.effective.office.tv.feature.stories.presentation.rating.common.TopRatingScreen
import coil3.ImageLoader
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@Composable
fun SportRatingContent(
    users: List<ClockifyUser>,
    imageLoader: ImageLoader,
) {
    val colors = LocalTvColorsPalette.current

    TopRatingScreen(
        users = users,
        backgroundColor = colors.sport,
        title = stringResource(Res.string.sport_title),
        logo = painterResource(Res.drawable.sport_logo)
    ) { user, place ->
        val hours = (user.totalSeconds / 3600.0).roundToInt()
        SportItem(
            modifier = Modifier.fillMaxWidth(),
            user = user,
            hours = hours,
            place = place,
            imageLoader = imageLoader
        )
    }
}

