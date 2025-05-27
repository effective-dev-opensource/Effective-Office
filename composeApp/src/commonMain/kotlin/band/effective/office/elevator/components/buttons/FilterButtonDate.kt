package band.effective.office.elevator.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.office.elevator.EffectiveTheme
import band.effective.office.elevator.MainRes
import band.effective.office.elevator.neutral15
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun FilterButtonDate(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier.size(28.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(5.dp),
        colors = ButtonDefaults.buttonColors(containerColor = neutral15)
    ) {
        Icon(
            painter = painterResource(MainRes.images.ic_date),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = EffectiveTheme.colors.icon.primary,
        )
    }
}
