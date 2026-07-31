package band.effective.office.tablet.core.ui.res

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.DrawableResource

/**
 * Фасад над загрузкой drawable-ресурсов.
 *
 * Сигнатуры повторяют `org.jetbrains.compose.resources.painterResource` /
 * `vectorResource` один-в-один, поэтому на месте вызова меняется только импорт.
 * На Android и iOS реализация просто делегирует в compose-resources; смысл фасада
 * в том, что платформа, где загрузчик ресурсов умеет не всё, может подставить свой.
 */
@Composable
expect fun painterResource(resource: DrawableResource): Painter

/**
 * См. [painterResource].
 */
@Composable
expect fun vectorResource(resource: DrawableResource): ImageVector
