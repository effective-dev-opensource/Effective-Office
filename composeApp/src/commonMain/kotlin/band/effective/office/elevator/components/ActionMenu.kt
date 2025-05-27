package band.effective.office.elevator.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import band.effective.office.elevator.EffectiveTheme
import band.effective.office.elevator.MainRes
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ActionMenu(
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
        ) {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More options")
            }
            DropdownMenu(
                modifier = modifier
                    .background(
                        color = EffectiveTheme.colors.background.primary,
                    )
                    .border(
                        width = 1.dp,
                        color = EffectiveTheme.colors.stroke.primary,
                        shape = RoundedCornerShape(16.dp)
                    ),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    modifier = modifier,
                    text = {
                        Text(
                            modifier = modifier.fillMaxWidth(),
                            text = stringResource(MainRes.strings.change),
                            style = EffectiveTheme.typography.mMedium,
                            color = EffectiveTheme.colors.text.primary,
                            textAlign = TextAlign.Center
                        )
                    },
                    onClick = {}
                )
                Divider(color = EffectiveTheme.colors.stroke.primary)
                DropdownMenuItem(
                    modifier = modifier,
                    text = {
                        Text(
                            modifier = modifier.fillMaxWidth(),
                            text = stringResource(MainRes.strings.delete),
                            style = EffectiveTheme.typography.mMedium,
                            color = EffectiveTheme.colors.text.error,
                            textAlign = TextAlign.Center
                        )
                    },
                    onClick = {}
                )
            }
        }
    }
}
