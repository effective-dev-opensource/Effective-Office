package band.effective.office.tablet.feature.fastBooking.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
 * Caps the card so the scrim stays visible around it and keeps taking the dismiss tap. The views
 * below size themselves off the width they are offered, so this is the only bound they need.
 */
private val MAX_FAST_BOOKING_WIDTH = 720.dp

/**
 * Caps the modal as a whole, clock included. The modal is centered in the scrim, so an unbounded
 * column lifts the clock by half of whatever the card of the current state adds, up over the
 * screen header; past the cap the content scrolls instead of growing.
 */
private val MAX_FAST_BOOKING_HEIGHT = 470.dp

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
        modifier = Modifier
            .widthIn(max = MAX_FAST_BOOKING_WIDTH)
            .heightIn(max = MAX_FAST_BOOKING_HEIGHT)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = DateDisplayMapper.formatTime(state.currentTime),
            style = MaterialTheme.typography.h2,
            color = LocalCustomColorsPalette.current.primaryTextAndIcon
        )
        Spacer(modifier = Modifier.height(30.dp))
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

/**
 * Displays a loading view with a spinner.
 *
 * @param onDismissRequest Callback to dismiss the modal
 */
@Composable
private fun LoadingView(
    onDismissRequest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.75f)
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
        Spacer(Modifier.height(30.dp))
    }
}

/**
 * Displays an error view when an operation fails.
 *
 * @param onDismissRequest Callback to dismiss the modal
 */
@Composable
private fun ErrorView(onDismissRequest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.75f)
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
