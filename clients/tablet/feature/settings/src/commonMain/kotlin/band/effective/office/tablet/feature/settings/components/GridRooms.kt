package band.effective.office.tablet.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette

@Composable
fun GridRooms(
    modifier: Modifier,
    data: List<String>,
    currentName: String,
    onChangeCurrentName: (name: String) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        content = {
            items(data) { item ->
                CardRoom(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10))
                        .background(LocalCustomColorsPalette.current.elevationBackground)
                        .selectable(
                            selected = false,
                            onClick = {
                                onChangeCurrentName(item)
                            }
                        ),
                    nameRoom = item,
                    currentNameRoom = currentName
                )
            }
        }
    )
}