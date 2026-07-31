package band.effective.office.tablet.core.ui.platform

import androidx.compose.ui.unit.Dp

/**
 * Эталонная короткая сторона окна в dp, к которой [ScaledUiDensity] приводит UI.
 *
 * Плотность Compose-сцены на Авроре задать нельзя: форк создаёт сцену как
 * `ComposeScene(density = Density(ru.auroraos.kmp.window.contentScale.toFloat()))`, а
 * `contentScale` приходит от системы вместе с окном. Поэтому dp-пространство фиксируем сами:
 * 800 dp по короткой стороне — это привычный 10" планшет (эталонный Android-планшет даёт ровно
 * столько же: 2560x1600 при плотности 2.0).
 *
 * На Android/iOS плотность даёт система, масштабирование выключено ([Dp] == 0).
 */
expect val uiScaleBaseline: Dp
