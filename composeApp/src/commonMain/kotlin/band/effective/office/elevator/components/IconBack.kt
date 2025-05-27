package band.effective.office.elevator.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.office.elevator.EffectiveTheme
import band.effective.office.elevator.MainRes
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun IconBack(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    iconRes: ImageResource = MainRes.images.back_button,
    tintColor: IconColor = IconColor.Black,
    size: IconSize = IconSize.Large
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState().value

    val iconSize = when (size) {
        IconSize.Small -> 16.dp
        IconSize.Large -> 24.dp
    }

    val buttonSize = when (size) {
        IconSize.Small -> 24.dp
        IconSize.Large -> 44.dp
    }

    val color = when (tintColor) {
        IconColor.Black -> EffectiveTheme.colors.icon.primary
        IconColor.Orange -> EffectiveTheme.colors.icon.accent
        IconColor.Gray -> EffectiveTheme.colors.icon.secondary
        IconColor.Red -> EffectiveTheme.colors.icon.accent
    }

    val pressedColor = when (tintColor) {
        IconColor.Black -> EffectiveTheme.colors.icon.accent
        IconColor.Orange -> EffectiveTheme.colors.icon.primary
        IconColor.Gray -> EffectiveTheme.colors.icon.accent
        IconColor.Red -> EffectiveTheme.colors.icon.secondary
    }

    IconButton(
        modifier = modifier.size(buttonSize),
        onClick = onClick,
        interactionSource = interactionSource,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = stringResource(MainRes.strings.back),
            modifier = modifier.size(iconSize),
            tint = if (isPressed) pressedColor else color
        )
    }
}

enum class IconSize {
    Small,
    Large
}

enum class IconColor {
    Black,
    Orange,
    Gray,
    Red
}