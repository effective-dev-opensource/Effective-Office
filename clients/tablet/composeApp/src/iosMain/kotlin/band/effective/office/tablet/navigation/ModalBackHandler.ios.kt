package band.effective.office.tablet.navigation

import androidx.compose.runtime.Composable

// No back gesture on iPad: the modal is dismissed by its cross button or a tap on the dim.
@Composable
actual fun ModalBackHandler(onBack: () -> Unit) = Unit
