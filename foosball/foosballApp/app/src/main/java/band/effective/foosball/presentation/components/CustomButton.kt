package band.effective.foosball.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import band.effective.foosball.ui.theme.PressedButton
import band.effective.foosball.ui.theme.Typography

@Composable
fun CustomButton(
    defaultColor: Color,
    pressedColor: Color = PressedButton,
    text: String,
    onClick: () -> Unit,
    width: Dp,
    height: Dp,
    cornerRadius: Dp = 30.dp,
    border: BorderStroke
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val buttonColor = if (isPressed) pressedColor else defaultColor

    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(top = 24.dp, bottom = 24.dp)
            .size(width = width, height = height)
            .background(color = buttonColor, shape = RoundedCornerShape(cornerRadius)),
        colors = buttonColors(buttonColor),
        interactionSource = interactionSource,
        shape = RoundedCornerShape(cornerRadius),
        border = border
    ) {
        Text(
            text = text,
            style = Typography.labelLarge
        )
    }
}

