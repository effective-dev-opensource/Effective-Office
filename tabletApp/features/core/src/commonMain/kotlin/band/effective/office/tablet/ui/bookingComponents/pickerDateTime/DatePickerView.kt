package band.effective.office.tablet.ui.bookingComponents.pickerDateTime

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.features.core.MainRes
import band.effective.office.tablet.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.ui.theme.header6
import epicarchitect.calendar.compose.basis.BasisDayOfMonthContent
import epicarchitect.calendar.compose.basis.BasisDayOfWeekContent
import epicarchitect.calendar.compose.basis.config.LocalBasisEpicCalendarConfig
import epicarchitect.calendar.compose.basis.contains
import epicarchitect.calendar.compose.basis.localizedBySystem
import epicarchitect.calendar.compose.basis.state.LocalBasisEpicCalendarState
import epicarchitect.calendar.compose.datepicker.EpicDatePicker
import epicarchitect.calendar.compose.datepicker.state.EpicDatePickerState
import epicarchitect.calendar.compose.datepicker.state.LocalEpicDatePickerState
import epicarchitect.calendar.compose.pager.state.EpicCalendarPagerState
import io.github.skeptick.libres.compose.painterResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerView(
    modifier: Modifier = Modifier,
    epicDatePickerState: EpicDatePickerState
) {
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = modifier) {
        DatePickerTitleView(
            epicDatePickerState = epicDatePickerState,
            onClickNextMonth = {
                scrollMonth(
                    coroutineScope = coroutineScope,
                    pagerState = epicDatePickerState.pagerState,
                    amount = 1
                )

            },
            onClickPreviousMonth = {
                scrollMonth(
                    coroutineScope = coroutineScope,
                    pagerState = epicDatePickerState.pagerState,
                    amount = -1
                )
            }
        )
        EpicDatePicker(
            state = epicDatePickerState,
            dayOfWeekContent = CustomDayOfWeekContent,
            dayOfMonthContent = CustomDayOfMonthContent,
            modifier = Modifier.background(Color.Transparent),
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DatePickerTitleView(
    epicDatePickerState: EpicDatePickerState,
    onClickNextMonth: () -> Unit,
    onClickPreviousMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                LocalCustomColorsPalette.current.mountainBackground,
                RoundedCornerShape(12.dp)
            )
            .fillMaxWidth().fillMaxHeight(0.15f)
            .clip(RoundedCornerShape(12.dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            modifier = Modifier.fillMaxHeight(),
            onClick = { onClickPreviousMonth() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
            )

        ) {
            Image(
                modifier = Modifier,
                painter = painterResource(MainRes.image.arrow_left),
                contentDescription = null
            )
        }
        Text(
            text = if (epicDatePickerState.selectedDates.isNotEmpty()) {
                stringFormat(epicDatePickerState.selectedDates.firstOrNull())
            } else {
                epicDatePickerState.pagerState.currentMonth.month.getDisplayName(
                    TextStyle.FULL_STANDALONE,
                    Locale("ru")
                ).lowercase() ?: ""
            },
            style = header6,
            color = LocalCustomColorsPalette.current.primaryTextAndIcon,
        )

        Button(
            modifier = Modifier.fillMaxHeight(),
            onClick = { onClickNextMonth() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
            )

        ) {
            Image(
                modifier = Modifier,
                painter = painterResource(MainRes.image.arrow_right),
                contentDescription = null
            )
        }
    }
}


private fun scrollMonth(
    coroutineScope: CoroutineScope,
    pagerState: EpicCalendarPagerState,
    amount: Int
) {
    coroutineScope.launch {
        pagerState.scrollMonths(amount)
    }
}

private val CustomDayOfWeekContent: BasisDayOfWeekContent = { dayOfWeek ->
    val config = LocalBasisEpicCalendarConfig.current
    Text(
        text = dayOfWeek.localizedBySystem(),
        textAlign = TextAlign.Center,
        color = config.contentColor,
        style = header6,
        maxLines = 1
    )
}

private val CustomDayOfMonthContent: BasisDayOfMonthContent = { date ->
    val basisState = LocalBasisEpicCalendarState.current!!
    val pickerState = LocalEpicDatePickerState.current!!
    val selectedDays = pickerState.selectedDates
    val selectionMode = pickerState.selectionMode

    val isSelected = remember(selectionMode, selectedDays, date) {
        when (selectionMode) {
            is EpicDatePickerState.SelectionMode.Range -> {
                if (selectedDays.isEmpty()) false
                else date in selectedDays.min()..selectedDays.max()
            }

            is EpicDatePickerState.SelectionMode.Single -> {
                date in selectedDays
            }
        }
    }

    Text(
        modifier = Modifier.alpha(
            if (date in basisState.currentMonth) 1.0f
            else 0.5f
        ),
        text = date.dayOfMonth.toString(),
        textAlign = TextAlign.Center,
        style = header6,
        maxLines = 1,
        color = if (isSelected) pickerState.config.selectionContentColor
        else pickerState.config.pagerConfig.basisConfig.contentColor
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun stringFormat(date: LocalDate?): String {
    val monthName = date?.month?.getDisplayName(TextStyle.FULL, Locale("ru")) ?: ""
    return "${date?.dayOfMonth} $monthName".lowercase()
}