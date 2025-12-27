package band.effective.office.tv.feature.stories.presentation.story

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import band.effective.office.tv.feature.stories.Res
import band.effective.office.tv.feature.stories.domain.model.EmployeeStoryType
import band.effective.office.tv.feature.stories.domain.model.StoryDomainModel
import band.effective.office.tv.feature.stories.story_anniversary
import band.effective.office.tv.feature.stories.story_birthday
import band.effective.office.tv.feature.stories.story_employee_photo
import band.effective.office.tv.feature.stories.story_intern
import band.effective.office.tv.feature.stories.story_month_anniversary
import band.effective.office.tv.feature.stories.story_new_employee
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import org.jetbrains.compose.resources.stringResource

/**
 * Employee story content - displays employee info with photo.
 * Shows birthday, anniversary, or welcome message.
 */
@Composable
fun EmployeeStoryContent(
    story: StoryDomainModel.EmployeeStory,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader,
) {
    val typography = LocalTvTypography.current
    val colors = LocalTvColorsPalette.current
    val sizes = LocalTvSizes.current

    val painter = rememberAsyncImagePainter(
        model = story.photoUrl,
        imageLoader = imageLoader
    )

    if (painter.state is AsyncImagePainter.State.Loading) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(bottom = sizes.paddingLarge)
                .fillMaxSize()
        ) {
            CircularProgressIndicator(
                color = colors.storyTextDark,
                strokeWidth = sizes.borderRegular,
                modifier = Modifier.size(sizes.loadCircleSize)
            )
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .fillMaxSize()
                .padding(vertical = sizes.storyContentPaddingVertical)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(sizes.gapMedium),
                modifier = Modifier
                    .padding(start = sizes.gapXXLarge, bottom = sizes.paddingLarge)
                    .width(sizes.storyTextBlockWidth)
            ) {
                if (story.isIntern) {
                    Text(
                        text = stringResource(Res.string.story_intern),
                        style = typography.bodyLarge,
                        color = colors.storyTextDark
                    )
                }

                Text(
                    text = story.name,
                    style = typography.displayMedium,
                    color = colors.storyTextDark
                )
                Text(
                    text = employeeDescription(story),
                    style = typography.headlineLarge,
                    color = colors.storyTextDark
                )
            }

            AsyncImage(
                model = story.photoUrl,
                contentDescription = stringResource(Res.string.story_employee_photo),
                modifier = Modifier
                    .size(sizes.storyAvatarSize)
                    .padding(sizes.paddingSmall)
                    .clip(CircleShape),
                imageLoader = imageLoader,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun employeeDescription(story: StoryDomainModel.EmployeeStory): String = when (story.type) {
    EmployeeStoryType.Birthday -> stringResource(Res.string.story_birthday)
    EmployeeStoryType.Anniversary -> stringResource(Res.string.story_anniversary, story.years)
    EmployeeStoryType.MonthAnniversary -> stringResource(Res.string.story_month_anniversary, story.months)
    EmployeeStoryType.NewEmployee -> stringResource(Res.string.story_new_employee)
}

