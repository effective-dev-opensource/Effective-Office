package band.effective.office.tablet.feature.fastBooking.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.common.CrossButtonView
import band.effective.office.tablet.core.ui.common.FailureFastSelectRoomView
import band.effective.office.tablet.core.ui.common.Loader
import band.effective.office.tablet.core.ui.common.SuccessFastSelectRoomView
import band.effective.office.tablet.core.ui.error
import band.effective.office.tablet.core.ui.inactivity.InactivityTracker
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h2
import band.effective.office.tablet.core.ui.theme.h4
import band.effective.office.tablet.core.ui.utils.DateDisplayMapper
import com.arkivanov.decompose.extensions.compose.stack.Children
import org.jetbrains.compose.resources.stringResource

/**
 * Main composable for the Fast Booking feature.
 * Displays different views based on the current state of the component.
 *
 * @param component The FastBookingComponent that manages the state and logic
 */
@Composable
fun FastBooking(component: FastBookingComponent) {
    val state by component.state.collectAsState()

    Children(stack = component.childStack, modifier = Modifier.padding(35.dp)) { modal ->
        Dialog(
            onDismissRequest = { component.sendIntent(Intent.OnCloseWindowRequest) },
            properties = DialogProperties(
                usePlatformDefaultWidth = modal.instance != FastBookingComponent.ModalConfig.LoadingModal
            )
        ) {
            InactivityTracker {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(50.dp))
                    Text(
                        text = DateDisplayMapper.formatTime(state.currentTime),
                        style = MaterialTheme.typography.h2,
                        color = LocalCustomColorsPalette.current.primaryTextAndIcon
                    )
                    Row(
                        modifier = Modifier.fillMaxHeight(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (val modalInstance = modal.instance) {
                            FastBookingComponent.ModalConfig.LoadingModal -> LoadingView(
                                onDismissRequest = { component.sendIntent(Intent.OnCloseWindowRequest) }
                            )

                            is FastBookingComponent.ModalConfig.FailureModal -> {
                                // Show failure view - either due to no available rooms or an error
                                if (state.isError) {
                                    ErrorView(onDismissRequest = { component.sendIntent(Intent.OnCloseWindowRequest) })
                                } else {
                                    FailureFastSelectRoomView(
                                        onDismissRequest = { component.sendIntent(Intent.OnCloseWindowRequest) },
                                        minutes = state.minutesLeft,
                                        room = modalInstance.room
                                    )
                                }
                            }

                            is FastBookingComponent.ModalConfig.SuccessModal -> {
                                // Only show success view if there's no error
                                if (state.isError) {
                                    ErrorView(onDismissRequest = { component.sendIntent(Intent.OnCloseWindowRequest) })
                                } else {
                                    SuccessFastSelectRoomView(
                                        roomName = modalInstance.room,
                                        finishTime = modalInstance.eventInfo.finishTime,
                                        close = { component.sendIntent(Intent.OnCloseWindowRequest) },
                                        onFreeRoomRequest = {
                                            component.sendIntent(
                                                Intent.OnFreeSelectRequest(
                                                    it
                                                )
                                            )
                                        },
                                        isLoading = state.isLoad
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Displays a loading view with a spinner.
 *
 * @param onDismissRequest Callback to dismiss the dialog
 */
@Composable
private fun LoadingView(
    onDismissRequest: () -> Unit
) {
    Box(contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .fillMaxHeight(0.4f)
                .clip(RoundedCornerShape(3))
                .background(LocalCustomColorsPalette.current.elevationBackground)
                .padding(35.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CrossButtonView(
                modifier = Modifier.fillMaxWidth(),
                onDismissRequest = onDismissRequest
            )
            Spacer(modifier = Modifier.height(40.dp))
            Loader()
        }
    }
}

/**
 * Displays an error view when an operation fails.
 *
 * @param onDismissRequest Callback to dismiss the dialog
 */
@Composable
private fun ErrorView(onDismissRequest: () -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .fillMaxHeight(0.4f)
                .clip(RoundedCornerShape(3))
                .background(LocalCustomColorsPalette.current.elevationBackground)
                .padding(35.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CrossButtonView(
                modifier = Modifier.fillMaxWidth(),
                onDismissRequest = onDismissRequest
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = stringResource(Res.string.error),
                style = MaterialTheme.typography.h4,
                minLines = 2,
                textAlign = TextAlign.Center,
                color = LocalCustomColorsPalette.current.primaryTextAndIcon
            )
            Spacer(Modifier.height(30.dp))
        }
    }
}
