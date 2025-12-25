package band.effective.office.tv.feature.menu.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import band.effective.office.tv.core.ui.Res
import band.effective.office.tv.core.ui.autoplay_menu_button
import band.effective.office.tv.core.ui.autoplay_menu_title
import band.effective.office.tv.core.ui.autoplay_zero_select
import band.effective.office.tv.core.ui.back_button
import band.effective.office.tv.core.ui.category_events
import band.effective.office.tv.core.ui.category_photos
import band.effective.office.tv.core.ui.category_stories
import band.effective.office.tv.core.ui.components.TextButton
import band.effective.office.tv.core.ui.icon_event_orange
import band.effective.office.tv.core.ui.icon_event_white
import band.effective.office.tv.core.ui.icon_photos_orange
import band.effective.office.tv.core.ui.icon_photos_white
import band.effective.office.tv.core.ui.icon_stories_orange
import band.effective.office.tv.core.ui.icon_stories_white
import band.effective.office.tv.core.ui.model.ContentCategory
import band.effective.office.tv.core.ui.play_icon
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvShapes
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import band.effective.office.tv.feature.menu.presentation.components.CategoryCard
import band.effective.office.tv.feature.menu.presentation.components.ProgressButton
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Auto-click delay for the start button in milliseconds.
 */
private const val AUTO_CLICK_DELAY_MS = 5000L

/**
 * Display order of content categories in menu and autoplay.
 */
private val CATEGORY_ORDER = listOf(
    ContentCategory.STORIES,
    ContentCategory.PHOTOS,
    ContentCategory.EVENTS
)

/**
 * Menu screen composable.
 * Uses MenuComponent for state management.
 *
 * @param component MenuComponent instance
 * @param modifier Modifier for the screen
 */
@Composable
fun MenuScreen(
    component: MenuComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.collectAsState()

    MenuScreenView(
        modifier = modifier,
        selectedCategories = state.selectedCategoriesSet,
        canStartAutoplay = state.canStartAutoplay,
        onToggleCategory = { category ->
            component.sendIntent(MenuIntent.ToggleCategory(category))
        },
        onStartAutoplay = {
            component.sendIntent(MenuIntent.StartAutoplay)
        },
        onBack = {
            component.sendIntent(MenuIntent.Back)
        },
    )
}

/**
 * Stateless menu screen view.
 * Contains all UI elements and layout.
 */
@Composable
fun MenuScreenView(
    modifier: Modifier = Modifier,
    selectedCategories: Set<ContentCategory>,
    canStartAutoplay: Boolean,
    onToggleCategory: (ContentCategory) -> Unit,
    onStartAutoplay: () -> Unit,
    onBack: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val typography = LocalTvTypography.current
    val colors = LocalTvColorsPalette.current
    val sizes = LocalTvSizes.current
    val shapes = LocalTvShapes.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(sizes.paddingSettingsScreen),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = stringResource(Res.string.autoplay_menu_title),
            style = typography.displaySmall,
            color = colors.textPrimary
        )

        Spacer(modifier = Modifier.height(sizes.gapXXLarge))

        // Menu items row - 3 categories
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(sizes.buttonHeightLarge),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(sizes.gapLarge))

            CATEGORY_ORDER.forEach { category ->
                CategoryMenuItem(
                    modifier = Modifier
                        .weight(1f)
                        .clip(shapes.medium),
                    category = category,
                    isSelected = category in selectedCategories,
                    onToggle = { onToggleCategory(category) }
                )

                Spacer(modifier = Modifier.width(sizes.gapLarge))
            }
        }

        Spacer(modifier = Modifier.height(sizes.gapXXLarge))

        // Start button with auto-click progress
        ProgressButton(
            onClick = {
                if (canStartAutoplay) {
                    onStartAutoplay()
                }
            },
            modifier = Modifier
                .height(sizes.buttonHeightSmall)
                .width(sizes.startButtonWidth)
                .clip(shapes.large),
            autoClickEnabled = canStartAutoplay,
            autoClickDelayMs = AUTO_CLICK_DELAY_MS,
            focusRequester = focusRequester,
        ) { isFocused ->
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier.size(sizes.playButtonIconSize),
                    painter = painterResource(Res.drawable.play_icon),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(sizes.gapTiny))
                val textAlpha = if (isFocused) 1f else 0.5f
                Text(
                    text = stringResource(Res.string.autoplay_menu_button),
                    style = typography.labelMedium,
                    color = colors.onPrimary.copy(alpha = textAlpha)
                )
            }
        }

        Spacer(modifier = Modifier.height(sizes.gapSmall))

        // Back button
        TextButton(
            text = stringResource(Res.string.back_button),
            onClick = onBack,
        )

        Spacer(modifier = Modifier.height(sizes.gapMedium))

        // Warning if nothing selected
        if (!canStartAutoplay) {
            Text(
                text = stringResource(Res.string.autoplay_zero_select),
                color = colors.textPrimary
            )
        }
    }

    LaunchedEffect(focusRequester) {
        delay(200)
        focusRequester.requestFocus()
    }
}

/**
 * Category menu item composable.
 */
@Composable
private fun CategoryMenuItem(
    modifier: Modifier = Modifier,
    category: ContentCategory,
    isSelected: Boolean,
    onToggle: () -> Unit,
) {
    val text = when (category) {
        ContentCategory.STORIES -> stringResource(Res.string.category_stories)
        ContentCategory.PHOTOS -> stringResource(Res.string.category_photos)
        ContentCategory.EVENTS -> stringResource(Res.string.category_events)
    }

    val iconRes = when (category) {
        ContentCategory.STORIES -> Res.drawable.icon_stories_orange
        ContentCategory.PHOTOS -> Res.drawable.icon_photos_orange
        ContentCategory.EVENTS -> Res.drawable.icon_event_orange
    }

    val activeIconRes = when (category) {
        ContentCategory.STORIES -> Res.drawable.icon_stories_white
        ContentCategory.PHOTOS -> Res.drawable.icon_photos_white
        ContentCategory.EVENTS -> Res.drawable.icon_event_white
    }

    CategoryCard(
        modifier = modifier,
        text = text,
        icon = painterResource(iconRes),
        activeIcon = painterResource(activeIconRes),
        isSelected = isSelected,
        onToggle = onToggle
    )
}
