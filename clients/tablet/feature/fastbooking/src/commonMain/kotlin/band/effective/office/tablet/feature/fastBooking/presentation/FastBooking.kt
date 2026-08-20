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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.common.CrossButtonView
import band.effective.office.tablet.core.ui.common.FailureFastSelectRoomView
import band.effective.office.tablet.core.ui.common.Loader
import band.effective.office.tablet.core.ui.common.SuccessFastSelectRoomView
import band.effective.office.tablet.core.ui.error
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h2
import band.effective.office.tablet.core.ui.theme.h4
import band.effective.office.tablet.core.ui.utils.DateDisplayMapper
import org.jetbrains.compose.resources.stringResource

/**
 * Main composable for the Fast Booking feature.
 * Displays a view per [FastBookingModal] in the current state.
 *
 * @param viewModel manages the fast-booking state and logic
 * @param onClose called when the flow requests to close (dismisses the overlay)
 */
@Composable
fun FastBooking(
    viewModel: FastBookingViewModel,
    onClose: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.closeEvents.collect { onClose() }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(35.dp),
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
            when (val modal = state.modal) {
                FastBookingModal.Loading -> LoadingView(
                    onDismissRequest = { viewModel.sendIntent(Intent.OnCloseWindowRequest) }
                )

                is FastBookingModal.Failure -> {
                    if (state.isError) {
                        ErrorView(onDismissRequest = { viewModel.sendIntent(Intent.OnCloseWindowRequest) })
                    } else {
                        FailureFastSelectRoomView(
                            onDismissRequest = { viewModel.sendIntent(Intent.OnCloseWindowRequest) },
                            minutes = state.minutesLeft,
                            room = modal.room
                        )
                    }
                }

                is FastBookingModal.Success -> {
                    if (state.isError) {
                        ErrorView(onDismissRequest = { viewModel.sendIntent(Intent.OnCloseWindowRequest) })
                    } else {
                        SuccessFastSelectRoomView(
                            roomName = modal.room,
                            finishTime = modal.eventInfo.finishTime,
                            close = { viewModel.sendIntent(Intent.OnCloseWindowRequest) },
                            onFreeRoomRequest = {
                                viewModel.sendIntent(Intent.OnFreeSelectRequest(it))
                            },
                            isLoading = state.isLoad
                        )
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
