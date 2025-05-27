package band.effective.office.elevator.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import band.effective.office.elevator.EffectiveTheme

@Composable
fun BookItemButton(
    modifier: Modifier = Modifier,
    number: Int = 1,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    val contentColor = if (isSelected) {
        EffectiveTheme.colors.text.primaryEvent
    } else {
        EffectiveTheme.colors.table.tableSelect
    }

    val backgroundColor = if (isSelected) {
        EffectiveTheme.colors.table.tableSelect
    } else {
        EffectiveTheme.colors.table.tableAvailable
    }

    Button(
        modifier = modifier.size(30.dp),
        onClick = onClick,
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = contentColor,
            containerColor = backgroundColor
        )
    ) {
        Text(
            text = number.toString(),
            textAlign = TextAlign.Center,
            style = EffectiveTheme.typography.sMedium
        )
    }
}
