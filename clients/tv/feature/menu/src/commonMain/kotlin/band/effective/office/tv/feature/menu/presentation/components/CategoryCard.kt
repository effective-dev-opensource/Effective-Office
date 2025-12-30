package band.effective.office.tv.feature.menu.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import band.effective.office.tv.core.ui.Res
import band.effective.office.tv.core.ui.checkbox_off
import band.effective.office.tv.core.ui.checkbox_on
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import org.jetbrains.compose.resources.painterResource

/**
 * Selectable menu item for category selection.
 * Shows icon, text, and checkbox based on selection state.
 * This component is specific to the Menu feature.
 */
@Composable
fun CategoryCard(
    modifier: Modifier = Modifier,
    text: String,
    icon: Painter,
    activeIcon: Painter,
    isSelected: Boolean,
    onToggle: () -> Unit,
    focusRequester: FocusRequester? = null,
    iconContentDescription: String? = null,
    checkboxContentDescription: String? = null,
) {
    var isFocused by remember { mutableStateOf(false) }
    val colors = LocalTvColorsPalette.current

    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (isFocused) colors.primary else colors.secondary,
        label = "category_card_background"
    )

    Box(
        modifier = modifier
            .semantics { selected = isSelected }
            .alpha(if (!isSelected && !isFocused) 0.5f else 1f)
            .background(animatedBackgroundColor)
            .fillMaxHeight(0.9f)
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .onFocusChanged { isFocused = it.isFocused }
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown &&
                    (event.key == Key.Enter || event.key == Key.DirectionCenter)
                ) {
                    onToggle()
                    true
                } else false
            }
            .clickable(onClick = onToggle)
            .focusable(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryIconWithTitle(
                text = text,
                isFocused = isFocused,
                icon = icon,
                activeIcon = activeIcon,
                iconContentDescription = iconContentDescription,
            )
        }

        CategoryCheckbox(
            isSelected = isSelected,
            checkboxContentDescription = checkboxContentDescription,
        )
    }
}

@Composable
private fun CategoryIconWithTitle(
    text: String,
    isFocused: Boolean,
    icon: Painter,
    activeIcon: Painter,
    iconContentDescription: String?,
) {
    val sizes = LocalTvSizes.current
    val colors = LocalTvColorsPalette.current
    val typography = LocalTvTypography.current

    Spacer(modifier = Modifier.width(sizes.gapLarge))

    Image(
        modifier = Modifier.size(sizes.menuItemIconSize),
        painter = if (isFocused) activeIcon else icon,
        contentDescription = iconContentDescription,
        contentScale = ContentScale.Fit
    )

    Spacer(modifier = Modifier.width(sizes.gapMedium))

    Text(
        text = text,
        color = colors.textPrimary,
        style = typography.labelLarge
    )
}

@Composable
private fun CategoryCheckbox(
    isSelected: Boolean,
    checkboxContentDescription: String?,
) {
    val sizes = LocalTvSizes.current

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(sizes.menuItemIconSize),
            painter = painterResource(
                if (isSelected) Res.drawable.checkbox_on
                else Res.drawable.checkbox_off
            ),
            contentDescription = checkboxContentDescription,
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(sizes.gapSmall))
    }
}