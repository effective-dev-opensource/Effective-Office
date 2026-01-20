package band.effective.office.tv.feature.stories.presentation.rating.duolingo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import band.effective.office.tv.feature.stories.*
import band.effective.office.tv.feature.stories.domain.model.DuolingoUser
import band.effective.office.tv.feature.stories.domain.model.DuolingoKey
import band.effective.office.tv.feature.stories.presentation.components.PlaceBadge
import band.effective.office.tv.feature.stories.presentation.rating.common.firstNameOf
import coil3.ImageLoader
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource

@Composable
fun DuolingoItem(
    modifier: Modifier = Modifier,
    user: DuolingoUser,
    indicator: String,
    place: Int,
    key: DuolingoKey,
    imageLoader: ImageLoader
) {
    val typography = LocalTvTypography.current
    val colors = LocalTvColorsPalette.current
    val sizes = LocalTvSizes.current
    val flagResources = mapLanguagesToFlags(user.countryLang)

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = sizes.gapSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(sizes.gapMedium)
        ) {
            Box(
                modifier = Modifier.size(sizes.ratingAvatarSize),
                contentAlignment = Alignment.BottomEnd
            ) {
                AsyncImage(
                    model = user.photo ?: "",
                    contentDescription = null,
                    imageLoader = imageLoader,
                    modifier = Modifier
                        .size(sizes.ratingAvatarSize)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(Res.drawable.duolingo_logo),
                    error = painterResource(Res.drawable.duolingo_logo),
                )

                PlaceBadge(
                    place = place,
                        textColor = colors.duolingo,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(sizes.gapMedium)
            ) {
                Text(
                    text = firstNameOf(user.name, user.username),
                    style = typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    color = colors.textPrimary,
                    maxLines = 1
                )
                if (flagResources.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(sizes.gapSmall)
                    ) {
                        flagResources.forEach { flag ->
                            Image(
                                painter = painterResource(flag),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(sizes.ratingSmallIconSize)
                                    .width(sizes.ratingSmallIconSize)
                            )
                        }
                    }
                }
            }
        }
        Text(
            text = indicator,
            style = typography.titleLarge,
            color = colors.textPrimary,
            textAlign = TextAlign.Right,
            maxLines = 1
        )
    }
}

