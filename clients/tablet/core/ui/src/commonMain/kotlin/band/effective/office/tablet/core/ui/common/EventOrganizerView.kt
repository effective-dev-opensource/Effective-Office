package band.effective.office.tablet.core.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.arrow_to_down
import band.effective.office.tablet.core.ui.platform.LocalModalHost
import band.effective.office.tablet.core.ui.platform.ModalHostState
import band.effective.office.tablet.core.ui.platform.SOFT_KEYBOARD_PRESS_GRACE
import band.effective.office.tablet.core.ui.platform.closeSoftKeyboard
import band.effective.office.tablet.core.ui.platform.fieldBottomIn
import band.effective.office.tablet.core.ui.platform.noteSoftKeyboardExpected
import band.effective.office.tablet.core.ui.res.painterResource
import band.effective.office.tablet.core.ui.selectbox_organizer_error
import band.effective.office.tablet.core.ui.selectbox_organizer_title
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h8
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

/**
 * Tells [host] where the row's bottom edge is, in the host's own space. Only ever an improvement on
 * what it knows: writing a null through would erase a good value with nothing left to restore it.
 */
private fun reportFieldBottom(host: ModalHostState?, row: LayoutCoordinates?) {
    if (host == null || row == null) return
    fieldBottomIn(host.containerCoords, row)?.let { host.focusedFieldBottom = it }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventOrganizerView(
    modifier: Modifier = Modifier,
    selectOrganizers: List<String>,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    onSelectItem: (String) -> Unit,
    onInput: (String) -> Unit,
    isInputError: Boolean,
    onDoneInput: (String) -> Unit,
    inputText: String
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // The gesture detector below is remembered on Unit, so it would hold the lambda and the state
    // it was handed on the first composition forever.
    val expandRequest by rememberUpdatedState(onExpandedChange)
    val isExpanded by rememberUpdatedState(expanded)
    var isFocused by remember { mutableStateOf(false) }

    // The row around the field, not the field itself: the list is sized to the row, so anchoring it
    // to the field would place it the row's horizontal padding away from where it is drawn from.
    var rowCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val modalHost = LocalModalHost.current

    // The net under the focus-lost branch: the field can be taken off screen mid-edit — the back
    // gesture, the inactivity reset — and no focus change is reported when that happens.
    DisposableEffect(modalHost) {
        onDispose {
            modalHost?.focusedFieldBottom = null
            if (isFocused) closeSoftKeyboard()
        }
    }

    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.selectbox_organizer_title),
            color = LocalCustomColorsPalette.current.secondaryTextAndIcon,
            style = MaterialTheme.typography.h8
        )
        Spacer(modifier = Modifier.height(10.dp))
        var rowSize by remember { mutableStateOf(Size.Zero) }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    rowSize = coordinates.size.toSize()
                    rowCoords = coordinates
                    // From layout rather than an effect: the edge moves when the platform resizes
                    // the scene under the keyboard, and a value cached off a coordinates reference
                    // would go on reporting where the field used to be.
                    if (isFocused) reportFieldBottom(modalHost, coordinates)
                }
                .clip(RoundedCornerShape(15.dp))
                .background(color = LocalCustomColorsPalette.current.elevationBackground)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier
                    // Initial pass and nothing consumed, so the field still takes the press
                    // itself. See the Aurora window model in clients/tablet/core/ui/README.md.
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown(
                                requireUnconsumed = false,
                                pass = PointerEventPass.Initial,
                            )
                            noteSoftKeyboardExpected()
                            // Where the field is belongs to the press for the reason the list
                            // does: on Aurora focus arrives once the keyboard already covers it.
                            reportFieldBottom(modalHost, rowCoords)
                            // onExpandedChange is a toggle, so a press with the list already
                            // open would close the list the press means to go on using.
                            val opening = !isExpanded
                            if (opening) expandRequest()
                            // Initial again, not Main: on Main the field's own detector has
                            // consumed the down before this wakes, and a consumed down reads as
                            // a cancelled gesture on every tap. Here an up is answered before
                            // consumption is looked at, so the only thing left to be consumed is
                            // a move — which nothing but a scroll above us takes.
                            if (waitForUpOrCancellation(PointerEventPass.Initial) != null) {
                                // Taken back if the focus never confirms the press: a value
                                // left behind has the host lifting the card for nobody. Same
                                // grace as the keyboard notice, so the two expire together.
                                scope.launch {
                                    delay(SOFT_KEYBOARD_PRESS_GRACE)
                                    if (!isFocused) modalHost?.focusedFieldBottom = null
                                }
                                return@awaitEachGesture
                            }
                            // The press never became a tap: the gesture left for the editor's
                            // scroll, and the field it would have focused never takes focus.
                            if (opening) expandRequest()
                            if (!isFocused) modalHost?.focusedFieldBottom = null
                        }
                    }
                    .onFocusChanged(
                        onFocusChanged = {
                            // Only a real loss counts: Compose reports the field unfocused once
                            // when it appears, and that is not the end of a session nobody started.
                            val wasFocused = isFocused
                            isFocused = it.isFocused
                            if (it.isFocused) {
                                // Said again on the way in: the layout callback only fires when
                                // the layout changes, and on Aurora focus arrives long after
                                // everything has settled.
                                reportFieldBottom(modalHost, rowCoords)
                            } else if (wasFocused) {
                                modalHost?.focusedFieldBottom = null
                                closeSoftKeyboard()
                            }
                        },
                    )
                    .focusRequester(focusRequester)
                    .fillMaxWidth(0.8f),
                value = inputText,
                singleLine = true,
                onValueChange = {
                    onInput(it)
                },
                placeholder = {
                    Text(
                        text = stringResource(
                            if (isInputError) Res.string.selectbox_organizer_error
                            else Res.string.selectbox_organizer_title
                        ),
                        color = LocalCustomColorsPalette.current.busyStatus
                    )
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // No defaultKeyboardAction(Done): it hides the keyboard synchronously, and
                        // on Aurora this lambda runs inside maliit's own key dispatch, where that
                        // deadlocks. Dropping the focus is what takes the keyboard down instead —
                        // the one door everything leaves editing through.
                        onDoneInput(inputText)
                        onExpandedChange()
                        // A frame late, because clearFocus() ends the text input session there and
                        // then, and on Aurora that tears the maliit session down from inside the
                        // dispatch still on the stack. The fork runs a bare launch inline, so only
                        // the frame await really leaves it.
                        scope.launch {
                            withFrameNanos { }
                            focusManager.clearFocus()
                        }
                    }
                ),
            )
            Image(
                modifier = Modifier,
                painter = painterResource(Res.drawable.arrow_to_down),
                contentDescription = null
            )
        }
        if (expanded) {
            OrganizerListPopup(anchorCoords = rowCoords) { listModifier ->
                LazyColumn(
                    modifier = listModifier
                        .width(with(density) { rowSize.width.toDp() })
                        .heightIn(max = 150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            LocalCustomColorsPalette.current.elevationBackground,
                            RoundedCornerShape(8.dp)
                        )
                        .border(3.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                ) {
                    items(selectOrganizers) { organizer ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectItem(organizer)
                                    // clearFocus() alone: freeFocus() releases focus that was
                                    // captured, and nothing here ever captures any.
                                    focusManager.clearFocus()
                                    onExpandedChange()
                                }
                                .padding(16.dp),
                        ) {
                            Text(
                                text = organizer,
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }


}
