package band.effective.office.tablet.core.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.arrow_to_down
import band.effective.office.tablet.core.ui.selectbox_organizer_error
import band.effective.office.tablet.core.ui.selectbox_organizer_title
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h8
import band.effective.office.tablet.core.ui.platform.LocalModalHost
import band.effective.office.tablet.core.ui.platform.closeSoftKeyboard
import band.effective.office.tablet.core.ui.platform.fieldBottomPx
import band.effective.office.tablet.core.ui.platform.noteSoftKeyboardExpected
import band.effective.office.tablet.core.ui.platform.popupIsSeparateScene
import band.effective.office.tablet.core.ui.res.painterResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private const val ORGANIZER_TAG = "OrganizerPicker"

/**
 * How long a press-time field-bottom report survives without focus confirming it. Covers the
 * fork's slow focus grant on Aurora (up to ~2 s seen); after that the press evidently did not
 * turn into editing.
 */
private const val FOCUS_GRACE_MS = 3_500L

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
    val scope = rememberCoroutineScope()


    var textFieldCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    // The field row's live coordinates, for reporting the bottom edge at focus time: the focus
    // change arrives without any relayout, so onGloballyPositioned — which only fires on moves
    // and on recomposition — cannot be counted on to fire again while focused. A reference, not
    // a cached position: it is asked where the row is at the moment of the write.
    var rowCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val density = LocalDensity.current

    // Tell whoever hosts this screen where the field's bottom edge is while it is being typed into,
    // so it can keep it clear of the keyboard. Written from the layout callback rather than an
    // effect: the edge moves when the platform resizes the scene under the keyboard, and a value
    // cached off a coordinates reference would keep reporting where the field used to be. Cleared
    // on focus loss and on the way out, or a stale edge would keep the host shifted.
    var isFocused by remember { mutableStateOf(false) }
    val modalHost = LocalModalHost.current
    DisposableEffect(modalHost) {
        onDispose {
            modalHost?.focusedFieldBottom = null
            // The net under the branch above: the field can be taken off screen mid-edit — back,
            // the inactivity reset — and no focus change is reported when that happens. Still
            // focused on the way out is what says the session is ours to close.
            Napier.i(tag = ORGANIZER_TAG) { "field gone, focused: $isFocused" }
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
        var mTextFieldSize by remember { mutableStateOf(Size.Zero) }

        Row(
            modifier = Modifier
                .fillMaxSize()
                // Everything keyed off the press rather than the focus that follows it: on Aurora
                // granting focus starts the maliit session synchronously, which costs a visible
                // second or two, and the keyboard is already rising during it. The press is the
                // only signal that comes first — so it opens the list, warns the platform, and
                // reports the field's position for the keyboard lift, instead of all three
                // trailing the keyboard. Initial pass, so the press is seen before the field
                // consumes it; the focus branch below is guarded against toggling the list shut.
                .pointerInput(expanded) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        Napier.i(tag = ORGANIZER_TAG) { "field pressed, expanded: $expanded" }
                        if (!expanded) onExpandedChange()
                        noteSoftKeyboardExpected()
                        if (!isFocused) {
                            modalHost?.focusedFieldBottom = rowCoords
                                ?.takeIf { c -> c.isAttached }
                                ?.let { c -> fieldBottomPx(modalHost.containerCoords, c) }
                            // Taken back if the focus never confirms the press — a stuck value
                            // would leave the host believing a field is being edited.
                            scope.launch {
                                delay(FOCUS_GRACE_MS)
                                if (!isFocused) modalHost?.focusedFieldBottom = null
                            }
                        }
                        do {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                        } while (event.changes.any { it.pressed })
                    }
                }
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    mTextFieldSize = coordinates.size.toSize()
                    rowCoords = coordinates
                    if (isFocused) {
                        // In the modal container's space, not the window's: on Aurora there is a
                        // rotation between the two and a window-space bottom is garbage there —
                        // see ModalHostState.
                        modalHost?.focusedFieldBottom =
                            fieldBottomPx(modalHost?.containerCoords, coordinates)
                    }
                }
                .clip(RoundedCornerShape(15.dp))
                .background(color = LocalCustomColorsPalette.current.elevationBackground)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier
                    .then(if (popupIsSeparateScene) Modifier.logKeyEvents() else Modifier)
                    .onFocusChanged(
                        onFocusChanged = {
                            // Only a real loss counts. Compose reports the field as unfocused once
                            // when it appears, and taking that for the end of editing would have
                            // us tearing down a keyboard session nobody has started yet.
                            val wasFocused = isFocused
                            isFocused = it.isFocused
                            Napier.i(tag = ORGANIZER_TAG) { "field focus: ${it.isFocused}" }
                            if (it.isFocused) {
                                // Written here and not left to onGloballyPositioned: focus arrives
                                // without a relayout, and the positioned callback stays silent
                                // until something moves — which is exactly what this write is
                                // supposed to cause.
                                modalHost?.focusedFieldBottom = rowCoords
                                    ?.takeIf { c -> c.isAttached }
                                    ?.let { c -> fieldBottomPx(modalHost.containerCoords, c) }
                                // Guarded: the press that granted this focus has usually opened
                                // the list already, and onExpandedChange is a toggle.
                                if (!expanded) onExpandedChange()
                            } else if (wasFocused) {
                                modalHost?.focusedFieldBottom = null
                                // The field is done being edited, so the keyboard's session is
                                // done too — on Aurora it has to be told. Everything that ends
                                // editing comes through here: Done, a name picked from the list,
                                // a tap on the dim, and the host catching a keyboard that went
                                // away on its own.
                                closeSoftKeyboard()
                            }
                        }
                    ).onSizeChanged({ mTextFieldSize = it.toSize() })
                    .focusRequester(focusRequester)
                    .fillMaxWidth(0.8f)
                    .onGloballyPositioned { textFieldCoords = it },
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
                        // No defaultKeyboardAction(Done) here: it hides the keyboard synchronously,
                        // and this lambda runs inside maliit's own key dispatch on Aurora, where
                        // Keyboard.close() then deadlocks against the in-flight send_input. Android
                        // and iOS take the keyboard down with the focus anyway, and on Aurora the
                        // focus branch below closes the session off the dispatch.
                        onDoneInput(inputText)
                        onExpandedChange()
                        // Dropping the focus, not freeing it: freeFocus() releases focus that was
                        // captured, and nothing here ever captures any, so it did nothing and the
                        // field stayed focused with the keyboard up. Everything that ends editing
                        // now leaves through the same door, the branch above.
                        //
                        // Deferred off the key dispatch: clearFocus() synchronously ends the text
                        // input session, whose cancel handler closes the maliit session — and on
                        // Aurora this lambda already runs inside maliit's own key dispatch, where
                        // that close deadlocks the process. A bare launch is not enough: the
                        // fork's FlushCoroutineDispatcher runs it inline, still inside the
                        // dispatch. withFrameNanos is a real suspension point, so the continuation
                        // arrives with the next frame — same thread, outside the dispatch.
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
            OrganizerListPopup(textFieldCoords = textFieldCoords) { listModifier ->
                OrganizerListBody(
                    modifier = listModifier,
                    width = with(density) { mTextFieldSize.width.toDp() },
                    selectOrganizers = selectOrganizers,
                    onSelectItem = onSelectItem,
                    onExpandedChange = onExpandedChange,
                    focusManager = focusManager,
                )
            }
        }
    }


}

/**
 * The list itself. Identical on every platform — only how it is placed inside the popup differs,
 * which is what [modifier] carries.
 */
@Composable
private fun OrganizerListBody(
    modifier: Modifier,
    width: Dp,
    selectOrganizers: List<String>,
    onSelectItem: (String) -> Unit,
    onExpandedChange: () -> Unit,
    focusManager: FocusManager,
) {
    Column(
        modifier = modifier
            .width(width)
            .heightIn(max = 150.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                LocalCustomColorsPalette.current.elevationBackground,
                RoundedCornerShape(8.dp)
            )
            .border(3.dp, Color.DarkGray, RoundedCornerShape(8.dp))
            .verticalScroll(rememberScrollState())
    ) {
        selectOrganizers.forEach { organizer ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSelectItem(organizer)
                        // clearFocus() alone, for the reason the Done action gives.
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

/**
 * Diagnostics for maliit input on Aurora: the fork delivers it as ordinary key events
 * (scene.sendKeyEvent), and text is only inserted when the event carries a codePoint. Whether the
 * field actually types is still unverified, so the instrument stays — but only where it is needed.
 */
private fun Modifier.logKeyEvents(): Modifier = onPreviewKeyEvent { keyEvent ->
    Napier.i(tag = ORGANIZER_TAG) {
        "key event: type=${keyEvent.type} key=${keyEvent.key.keyCode} " +
            "codePoint=${keyEvent.utf16CodePoint}"
    }
    false
}
