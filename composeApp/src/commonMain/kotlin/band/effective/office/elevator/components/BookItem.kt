package band.effective.office.elevator.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.office.elevator.EffectiveTheme

data class BookItem(
    val name: String,
    val availableTables: List<Int>
)

@Composable
fun BookItem(
    bookItem: BookItem,
    selectedTables: Set<Pair<String, Int>>,
    onTableClick: (roomName: String, tableNumber: Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = bookItem.name,
                style = EffectiveTheme.typography.mMedium,
                color = EffectiveTheme.colors.text.primary,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                bookItem.availableTables.forEach { table ->
                    BookItemButton(
                        number = table,
                        isSelected = selectedTables.contains(bookItem.name to table),
                        onClick = { onTableClick(bookItem.name, table) }
                    )
                }
            }
        }
        Divider(color = EffectiveTheme.colors.divider.primary, thickness = 1.dp)
    }
}
