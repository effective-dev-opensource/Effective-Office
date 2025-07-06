package band.effective.office.tablet.root

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import band.effective.office.tablet.feature.bookingEditor.presentation.BookingEditor
import band.effective.office.tablet.feature.bookingEditor.presentation.BookingEditorComponent
import band.effective.office.tablet.feature.fastBooking.presentation.FastBooking
import band.effective.office.tablet.feature.fastBooking.presentation.FastBookingComponent
import band.effective.office.tablet.feature.main.presentation.freeuproom.FreeSelectRoomComponent
import band.effective.office.tablet.feature.main.presentation.freeuproom.FreeSelectRoomView
import band.effective.office.tablet.feature.main.presentation.main.MainScreen
import band.effective.office.tablet.feature.settings.SettingsScreen
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@Composable
fun Root(component: RootComponent) {
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Children(
            stack = component.childStack,
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.MainChild -> MainScreen(instance.component)
                is RootComponent.Child.SettingsChild -> SettingsScreen(instance.component)
            }
        }

        // Display modal windows
        val activeWindowSlot by component.modalWindowSlot.subscribeAsState()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = if (activeWindowSlot.child != null)
                        Color.Black.copy(alpha = 0.9f)
                    else Color.Transparent
                )
        ) {
            when (val activeComponent = activeWindowSlot.child?.instance) {
                is FreeSelectRoomComponent -> FreeSelectRoomView(freeSelectRoomComponent = activeComponent)
                is BookingEditorComponent -> BookingEditor(component = activeComponent)
                is FastBookingComponent -> FastBooking(component = activeComponent)
                else -> {}
            }
        }
    }
}
