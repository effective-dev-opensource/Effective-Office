package band.effective.office.tv.feature.stories.presentation.story

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvShapes
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import band.effective.office.tv.core.ui.theme.robotoFontFamily
import band.effective.office.tv.core.ui.theme.TvColorsPalette
import band.effective.office.tv.feature.stories.Res
import band.effective.office.tv.feature.stories.domain.model.StoryDomainModel
import band.effective.office.tv.feature.stories.no_stories_today
import org.jetbrains.compose.resources.stringResource

/**
 * Background color per story type.
 * Uses colors from theme palette.
 * Employee stories use white/light background, ratings use colored backgrounds.
 * When story is null (loading state), use dark background.
 */
fun storyBackground(
    story: StoryDomainModel?,
    colors: TvColorsPalette
): Color = when (story) {
    is StoryDomainModel.DuolingoStory -> colors.duolingo
    is StoryDomainModel.SportStory -> colors.sport
    is StoryDomainModel.SupernovaStory -> colors.supernova
    is StoryDomainModel.EmployeeStory -> colors.storyBackground
    null -> colors.background  // Dark background for loading state
}

/**
 * Stories progress indicator - shows current position in story list.
 * Each story has a progress bar that fills as the story plays.
 */
@Composable
fun StoryIndicator(
    countStories: Int,
    currentStoryIndex: Int,
    modifier: Modifier = Modifier,
    progress: Float = 1f,
    activeColor: Color,
    inactiveColor: Color,
) {
    val sizes = LocalTvSizes.current
    val shapes = LocalTvShapes.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(sizes.storyIndicatorSpacing)
    ) {
        repeat(countStories) { index ->
            val value = when {
                index < currentStoryIndex -> 1f
                index == currentStoryIndex -> progress.coerceIn(0f, 1f)
                else -> 0f
            }
            LinearProgressIndicator(
                progress = { value },
                modifier = Modifier
                    .weight(1f)
                    .height(sizes.storyIndicatorHeight)
                    .clip(shapes.small),
                color = activeColor,
                trackColor = inactiveColor
            )
        }
    }
}

/**
 * Empty state screen when no stories are available today.
 */
@Composable
fun NoStoriesScreen() {
    val colors = LocalTvColorsPalette.current
    val typography = LocalTvTypography.current
    val sizes = LocalTvSizes.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(sizes.gapXXLarge * 3),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.no_stories_today),
                style = typography.displaySmall.copy(fontFamily = robotoFontFamily()),
                color = colors.textPrimary
            )
        }
    }
}

