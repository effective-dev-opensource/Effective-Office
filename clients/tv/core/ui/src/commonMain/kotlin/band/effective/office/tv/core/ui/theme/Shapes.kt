package band.effective.office.tv.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

/**
 * Shape values from tvApp usage.
 */
val TvShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(40.dp),
    extraLarge = RoundedCornerShape(210.dp)
)

/**
 * Composition local for providing shapes
 */
val LocalTvShapes = staticCompositionLocalOf { TvShapes }
