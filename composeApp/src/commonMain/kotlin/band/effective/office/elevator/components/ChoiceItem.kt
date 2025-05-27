package band.effective.office.elevator.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import band.effective.office.elevator.EffectiveTheme

@Composable
fun ChoiceItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    isActive: Boolean,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState().value

    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPressed) {
                EffectiveTheme.colors.button.secondaryPress
            } else {
                EffectiveTheme.colors.button.secondaryNormal
            }
        ),
        border = BorderStroke(
            width = 1.dp, color =
            if (isActive) {
                EffectiveTheme.colors.stroke.accent
            } else Color.Transparent
        ),
        interactionSource = interactionSource,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            textAlign = TextAlign.Start,
            style = EffectiveTheme.typography.sMedium,
            color = EffectiveTheme.colors.text.primary,
        )
    }
}
