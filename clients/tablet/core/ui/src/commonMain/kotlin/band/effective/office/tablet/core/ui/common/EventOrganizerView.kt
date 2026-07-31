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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.arrow_to_down
import band.effective.office.tablet.core.ui.selectbox_organizer_error
import band.effective.office.tablet.core.ui.selectbox_organizer_title
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h8
import band.effective.office.tablet.core.ui.platform.ForcedLandscape
import band.effective.office.tablet.core.ui.platform.ScaledUiDensity
import band.effective.office.tablet.core.ui.res.painterResource
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

private const val ORGANIZER_TAG = "OrganizerPicker"

// Gap between the field and the expanded list, in px (px do not depend on a substituted density).
private const val LIST_GAP_PX = 8

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


    var textFieldCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val density = LocalDensity.current

    // The popup covers the whole window and rotates to landscape itself (see below): its layer
    // lives in the fork's unrotated window, so the list is positioned by hand inside it.
    val fullWindowPositionProvider = remember {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset = IntOffset.Zero
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
                }
                .clip(RoundedCornerShape(15.dp))
                .background(color = LocalCustomColorsPalette.current.elevationBackground)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier.onPreviewKeyEvent { keyEvent ->
                    // The fork delivers maliit input as ordinary key events (scene.sendKeyEvent),
                    // and text is only inserted when the event carries a codePoint. Log what arrives.
                    Napier.i(tag = ORGANIZER_TAG) {
                        "key event: type=${keyEvent.type} key=${keyEvent.key.keyCode} " +
                            "codePoint=${keyEvent.utf16CodePoint}"
                    }
                    false
                }.onFocusChanged(
                    onFocusChanged = {
                        if (it.isFocused) {
                            onExpandedChange()
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
                        defaultKeyboardAction(ImeAction.Done)
                        onDoneInput(inputText)
                        onExpandedChange()
                        focusRequester.freeFocus()
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
            Popup(
                popupPositionProvider = fullWindowPositionProvider,
                onDismissRequest = { },
            ) {
                // A popup window is sized to its content by default, and we move the list
                // ourselves with offset — so the layer is stretched to fill the window, or the
                // offset list would fall outside its own window and be clipped.
                // The popup layer is also a separate scene in the fork's unrotated window, so the
                // rotation is re-applied here (same as for modals in DialogBackgroundDim).
                Box(modifier = Modifier.fillMaxSize()) {
                    ForcedLandscape {
                        // Its own scene means the system density too, so re-apply the scale.
                        ScaledUiDensity(modifier = Modifier.fillMaxSize()) {
                            var listSize by remember { mutableStateOf(IntSize.Zero) }
                            // positionInWindow() reports coordinates in the UNROTATED content
                            // layout, not in the physical window; the rotation is a drawing effect
                            // and does not touch them, so they are used as-is.
                            val anchor = textFieldCoords?.positionInWindow()?.let {
                                IntOffset(it.x.roundToInt(), it.y.roundToInt())
                            } ?: IntOffset.Zero

                            Column(
                                modifier = Modifier
                                    .offset {
                                        // The list opens upward from the field, with a small gap.
                                        IntOffset(
                                            x = anchor.x,
                                            y = (anchor.y - listSize.height - LIST_GAP_PX)
                                                .coerceAtLeast(0),
                                        )
                                    }
                                    .onSizeChanged { listSize = it }
                                    .width(with(density) { mTextFieldSize.width.toDp() })
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
                                                focusRequester.freeFocus()
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
        }
    }


}
