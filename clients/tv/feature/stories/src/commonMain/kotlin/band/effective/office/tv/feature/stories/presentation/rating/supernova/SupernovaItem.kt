package band.effective.office.tv.feature.stories.presentation.rating.supernova

import androidx.compose.foundation.Image
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
import band.effective.office.tv.feature.stories.currency
import band.effective.office.tv.feature.stories.domain.model.supernova.SupernovaScore
import band.effective.office.tv.feature.stories.presentation.components.PlaceBadge
import band.effective.office.tv.feature.stories.supernova
import band.effective.office.tv.feature.stories.presentation.rating.common.firstNameOf
import coil3.ImageLoader
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource

/**
 * Supernova rating item - shows user avatar, name and score with currency icon.
 * Layout matches the original TV app style.
 */
@Composable
fun SupernovaItem(
    modifier: Modifier = Modifier,
    user: SupernovaScore,
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
                    placeholder = painterResource(Res.drawable.supernova),
                    error = painterResource(Res.drawable.supernova),
                )

                PlaceBadge(
                    place = place,
                    textColor = colors.supernova,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
            Spacer(modifier = Modifier.width(sizes.gapMedium))
            Text(
                text = firstNameOf(user.name, user.id),
                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
                maxLines = 1
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${user.score}",
                style = typography.titleLarge,
                color = colors.textPrimary,
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.width(sizes.gapTiny))
            Image(
                modifier = Modifier.size(sizes.ratingSmallIconSize),
                painter = painterResource(Res.drawable.currency),
                contentDescription = null
            )
        }
    }
}

