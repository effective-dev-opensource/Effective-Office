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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.arrow_to_down
import band.effective.office.tablet.core.ui.platform.LocalModalHost
import band.effective.office.tablet.core.ui.platform.fieldBottomPx
import band.effective.office.tablet.core.ui.platform.closeSoftKeyboard
import band.effective.office.tablet.core.ui.platform.noteSoftKeyboardExpected
import band.effective.office.tablet.core.ui.platform.popupIsSeparateScene
import band.effective.office.tablet.core.ui.res.painterResource
import band.effective.office.tablet.core.ui.selectbox_organizer_error
import band.effective.office.tablet.core.ui.selectbox_organizer_title
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h8
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

private const val ORGANIZER_TAG = "OrganizerPicker"

/**
 * How long a press waits for the focus that should follow it before its report of the field's
 * position is taken back. Covers the Aurora fork's slow focus grant — about two seconds — and
 * nothing on the other platforms, where focus arrives with the press.
 */
private const val PRESS_TO_FOCUS_GRACE_MS = 3000L

/**
 * Bottom edge of this node in [container]'s space, or `null` if either has left the tree and its
 * position means nothing any more. Never window space — see [ModalHostState].
 */
private fun LayoutCoordinates.bottomIn(container: LayoutCoordinates?): Int? =
    takeIf { it.isAttached }?.let { fieldBottomPx(container, it) }

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

    // The gesture detector below is remembered on `Unit`, so it would hold the lambda it was given
    // on the first composition forever. Every other caller of this one reaches it from a callback
    // that recomposes with it; the press handler does not.
    val expandRequest by rememberUpdatedState(onExpandedChange)


    // The row around the field — what the modal is asked to lift clear of the keyboard, and what
    // the expanded list is both sized to and anchored on. Kept so a press can report it before the
    // field has focus to report it from.
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
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    mTextFieldSize = coordinates.size.toSize()
                    rowCoords = coordinates
                    if (isFocused) {
                        coordinates.bottomIn(modalHost?.containerCoords)
                            ?.let { modalHost?.focusedFieldBottom = it }
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
                    // The press, not the focus that follows it. On Aurora the fork starts the
                    // keyboard session before granting focus and takes a second or two over it, so
                    // a modal that waits for focus moves long after the keyboard has covered the
                    // field. The press warns the platform and reports where the field is, and the
                    // shift happens at once. Initial pass and nothing consumed, so the field still
                    // takes the press itself.
                    //
                    // On the field and not on the row around it. The row also holds the arrow, and
                    // an arrow that starts the press path is an arrow that opens the list and lifts
                    // the card for a keyboard that never comes — the field never took focus, so the
                    // grace timer below simply puts the card back down. The arrow has no click of
                    // its own on any platform, and now it has no effect through the row either.
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                            noteSoftKeyboardExpected()
                            if (!isFocused) {
                                val bottom = rowCoords?.bottomIn(modalHost?.containerCoords)
                                Napier.i(tag = ORGANIZER_TAG) { "field pressed, bottom: $bottom" }
                                // Only ever an improvement on what the host knows. Writing the null
                                // through would erase a good value and leave nothing to restore it:
                                // the layout callback that would have reported one fires when the
                                // layout changes, and by the time a field is pressed it long has.
                                if (bottom != null) modalHost?.focusedFieldBottom = bottom
                                // The list belongs to the press for the same reason the shift does.
                                // Opened from the focus callback instead, it waited out the whole
                                // maliit handshake and arrived after the keyboard — a press, a jump,
                                // a pause, a keyboard, another pause, a list. Nothing about the list
                                // needs focus: the names are already loaded and the field is already
                                // being aimed at. Android and iOS grant focus on the same gesture,
                                // so there the two moments were never apart.
                                expandRequest()
                                // Taken back if the focus never confirms the press: a value left
                                // behind would have the host believing a field is being edited, and
                                // the first tap on the dim would go to dismissing a keyboard that
                                // never came.
                                scope.launch {
                                    delay(PRESS_TO_FOCUS_GRACE_MS)
                                    if (!isFocused) modalHost?.focusedFieldBottom = null
                                }
                            }
                        }
                    }
                    .onFocusChanged(
                        onFocusChanged = {
                            // Only a real loss counts. Compose reports the field as unfocused once
                            // when it appears, and taking that for the end of editing would have
                            // us tearing down a keyboard session nobody has started yet.
                            val wasFocused = isFocused
                            isFocused = it.isFocused
                            Napier.i(tag = ORGANIZER_TAG) { "field focus: ${it.isFocused}" }
                            if (it.isFocused) {
                                // Where the field is, said again on the way in. The layout callback
                                // reports it too, but it only fires when the layout changes, and on
                                // Aurora focus arrives seconds after everything has settled — so
                                // for the host it may be this or nothing.
                                rowCoords?.bottomIn(modalHost?.containerCoords)
                                    ?.let { bottom -> modalHost?.focusedFieldBottom = bottom }
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
                        // deadlocks. Android and iOS take the keyboard down with the focus anyway,
                        // which is what the branch below is for.
                        onDoneInput(inputText)
                        onExpandedChange()
                        // Dropping the focus, not freeing it: freeFocus() releases focus that was
                        // captured, and nothing here ever captures any, so it did nothing and the
                        // field stayed focused with the keyboard up. Everything that ends editing
                        // leaves through the same door — the focus-lost branch above.
                        //
                        // A frame late, on purpose. clearFocus() ends the text input session there
                        // and then, and on Aurora the session's teardown closes the maliit one from
                        // inside the key dispatch that is still on the stack — the deadlock again.
                        // A bare launch does not help: the fork runs it inline. withFrameNanos is a
                        // real suspension, so the rest arrives with the next frame, on the same
                        // thread but outside the dispatch. Android and iOS pay one frame for it.
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
