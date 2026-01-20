package band.effective.office.tv.feature.stories.presentation.rating.sport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import band.effective.office.tv.core.ui.theme.robotoFontFamily
import band.effective.office.tv.feature.stories.Res
import band.effective.office.tv.feature.stories.sport_hours
import band.effective.office.tv.feature.stories.domain.model.sport.ClockifyUser
import band.effective.office.tv.feature.stories.presentation.components.PlaceBadge
import band.effective.office.tv.feature.stories.sport_logo
import band.effective.office.tv.feature.stories.presentation.rating.common.firstNameOf
import coil3.ImageLoader
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Sport rating item - shows user avatar, name and hours worked.
 * Layout matches the original TV app style.
 */
@Composable
fun SportItem(
    modifier: Modifier = Modifier,
    user: ClockifyUser,
    hours: Int,
    place: Int,
    imageLoader: ImageLoader
) {
    val typography = LocalTvTypography.current
    val colors = LocalTvColorsPalette.current
    val sizes = LocalTvSizes.current

    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(sizes.ratingAvatarSize)) {
                AsyncImage(
                    model = user.photo,
                    contentDescription = null,
                    imageLoader = imageLoader,
                    modifier = Modifier
                        .size(sizes.ratingAvatarSize)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(Res.drawable.sport_logo),
                    error = painterResource(Res.drawable.sport_logo),
                )

                PlaceBadge(
                    place = place,
                    textColor = colors.sport,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
            Spacer(modifier = Modifier.width(sizes.gapMedium))
            Text(
                text = firstNameOf(user.name),
                style = typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = colors.textPrimary,
                maxLines = 1
            )
        }
        Text(
            text = stringResource(Res.string.sport_hours, hours),
            style = typography.titleLarge,
            color = colors.textPrimary,
            textAlign = TextAlign.End,
            maxLines = 1
        )
    }
}

