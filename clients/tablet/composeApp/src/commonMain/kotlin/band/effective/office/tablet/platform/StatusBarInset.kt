package band.effective.office.tablet.platform

import androidx.compose.ui.unit.Dp

/**
 * Отступ под статус-бар Авроры. Применяется ВНУТРИ повёрнутого содержимого (см. AppRoot),
 * поэтому в альбоме оказывается сверху, а не сбоку. На Android/iOS равен нулю — там
 * системные бары закрывает `systemBarsPadding()`.
 */
expect val statusBarInset: Dp
