package band.effective.office.tv.feature.stories.presentation.rating.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import band.effective.office.tv.core.ui.theme.LocalTvSizes

const val ROWS_COUNT = 5
private const val MAX_ITEMS = 10

/**
 * Horizontal grid layout for displaying top 10 rating items.
 * Shows 5 rows and 2 columns (items fill left column first, then right).
 */
@Composable
fun <T> TopRating(
    users: List<T>,
    item: @Composable LazyGridItemScope.(T, index: Int) -> Unit
) {
    val sizes = LocalTvSizes.current
    val displayUsers = users.take(MAX_ITEMS)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val columnGap = sizes.gapXXLarge
        val columnWidth = (maxWidth - columnGap) / 2f
        val rowHeight = (maxHeight - sizes.gapMedium * 4) / 5f

        LazyHorizontalGrid(
            rows = GridCells.Fixed(ROWS_COUNT),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(columnGap),
            verticalArrangement = Arrangement.spacedBy(sizes.gapMedium),
        ) {
            itemsIndexed(displayUsers) { index, user ->
                Box(
                    modifier = Modifier
                        .width(columnWidth)
                        .height(rowHeight)
                ) {
                    item(user, index + 1)
                }
            }
        }
    }
}