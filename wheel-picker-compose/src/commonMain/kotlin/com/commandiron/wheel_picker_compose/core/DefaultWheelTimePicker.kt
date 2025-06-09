package com.commandiron.wheel_picker_compose.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.commandiron.wheel_picker_compose.utils.Max
import com.commandiron.wheel_picker_compose.utils.Min
import com.commandiron.wheel_picker_compose.utils.getCurrentTime
import com.commandiron.wheel_picker_compose.utils.isAfter
import com.commandiron.wheel_picker_compose.utils.isBefore
import com.commandiron.wheel_picker_compose.utils.of
import com.commandiron.wheel_picker_compose.utils.truncatedToMinute
import com.commandiron.wheel_picker_compose.utils.withHour
import com.commandiron.wheel_picker_compose.utils.withMinute
import kotlinx.datetime.LocalTime


@Composable
internal fun DefaultWheelTimePicker(
    modifier: Modifier = Modifier,
    startTime: LocalTime = getCurrentTime(),
    minTime: LocalTime = LocalTime.Min,
    maxTime: LocalTime = LocalTime.Max,
    timeFormat: TimeFormat = TimeFormat.HOUR_24,
    size: DpSize = DpSize(128.dp, 128.dp),
    rowCount: Int = 3,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    textColor: Color = LocalContentColor.current,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onSnappedTime: (snappedTime: SnappedTime, timeFormat: TimeFormat) -> Unit = { _, _ -> },
) {

    var snappedTime by remember { mutableStateOf(startTime.truncatedToMinute()) }

    fun onTimeChanged() {
        onSnappedTime(SnappedTime.Hour(localTime = snappedTime, index = 0), timeFormat)
    }

    val hours = (0..23).map {
        Hour(
            text = it.toString().padStart(2, '0'),
            value = it,
            index = it
        )
    }
    val amPmHours = (1..12).map {
        AmPmHour(
            text = it.toString(),
            value = it,
            index = it - 1
        )
    }

    val minutes = (0..59 step 5).map {
        Minute(
            text = it.toString().padStart(2, '0'),
            value = it,
            index = it.div(5)
        )
    }

    val amPms = listOf(
        AmPm(
            text = "AM",
            value = AmPmValue.AM,
            index = 0
        ),
        AmPm(
            text = "PM",
            value = AmPmValue.PM,
            index = 1
        )
    )

    var snappedAmPm by remember {
        mutableStateOf(
            amPms.find { it.value == amPmValueFromTime(startTime) } ?: amPms[0]
        )
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (selectorProperties.enabled().value) {
            Surface(
                modifier = Modifier.size(size.width, size.height / rowCount),
                shape = selectorProperties.shape().value,
                color = selectorProperties.color().value,
                border = selectorProperties.border().value
            ) {}
        }
        Row {
            //Hour
            WheelTextPicker(
                size = DpSize(
                    width = size.width / if (timeFormat == TimeFormat.HOUR_24) 2 else 3,
                    height = size.height
                ),
                texts = if (timeFormat == TimeFormat.HOUR_24) hours.map { it.text } else amPmHours.map { it.text },
                rowCount = rowCount,
                style = textStyle,
                color = textColor,
                startIndex = if (timeFormat == TimeFormat.HOUR_24) {
                    hours.find { it.value == startTime.hour }?.index ?: 0
                } else amPmHours.find { it.value == localTimeToAmPmHour(startTime) }?.index ?: 0,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    enabled = false
                ),
                onSnappedIndexChanged = { snappedIndex ->

                    val newHour = if (timeFormat == TimeFormat.HOUR_24) {
                        hours.getOrNull(snappedIndex)?.value
                    } else {
                        amPmHourToHour24(
                            amPmHours.getOrNull(snappedIndex)?.value ?: 0,
                            snappedTime.minute,
                            snappedAmPm.value
                        )
                    }

                    newHour?.let {

                        val newTime = snappedTime.withHour(newHour)

                        if (!newTime.isBefore(minTime) && !newTime.isAfter(maxTime)) {
                            snappedTime = newTime
                            onTimeChanged()
                        }
                    }
                }
            )
            // Minute
            WheelTextPicker(
                size = DpSize(
                    width = size.width / if (timeFormat == TimeFormat.HOUR_24) 2 else 3,
                    height = size.height
                ),
                texts = minutes.map { it.text },
                rowCount = rowCount,
                style = textStyle,
                color = textColor,
                startIndex = minutes.find { it.value == startTime.minute }?.index ?: 0,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    enabled = false
                ),
                onSnappedIndexChanged = { snappedIndex ->

                    val newMinute = minutes.getOrNull(snappedIndex)?.value
                    newMinute?.let {
                        val newTime = snappedTime.withMinute(newMinute)
                        if (!newTime.isBefore(minTime) && !newTime.isAfter(maxTime)) {
                            snappedTime = newTime
                            onTimeChanged()
                        }
                    }
                }
            )
            //AM_PM
            if (timeFormat == TimeFormat.AM_PM) {
                WheelTextPicker(
                    size = DpSize(
                        width = size.width / 3,
                        height = size.height
                    ),
                    texts = amPms.map { it.text },
                    rowCount = rowCount,
                    style = textStyle,
                    color = textColor,
                    startIndex = amPms.find { it.value == amPmValueFromTime(startTime) }?.index
                        ?: 0,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    onSnappedIndexChanged = { snappedIndex ->

                        val newAmPm = amPms.getOrNull(snappedIndex)
                        newAmPm?.let {
                            snappedAmPm = newAmPm
                            val currentAmPmHour = localTimeToAmPmHour(snappedTime)
                            val newHour = amPmHourToHour24(currentAmPmHour, snappedTime.minute, newAmPm.value)
                            val newTime = snappedTime.withHour(newHour)
                            if (!newTime.isBefore(minTime) && !newTime.isAfter(maxTime)) {
                                snappedTime = newTime
                                onTimeChanged()
                            }
                        }
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .size(
                    width = if (timeFormat == TimeFormat.HOUR_24) {
                        size.width
                    } else size.width * 2 / 3,
                    height = size.height / 3
                )
                .align(
                    alignment = if (timeFormat == TimeFormat.HOUR_24) {
                        Alignment.Center
                    } else Alignment.CenterStart
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ":",
                style = textStyle,
                color = textColor
            )
        }
    }
}

enum class TimeFormat {
    HOUR_24, AM_PM
}

private data class Hour(
    val text: String,
    val value: Int,
    val index: Int
)

private data class AmPmHour(
    val text: String,
    val value: Int,
    val index: Int
)

internal fun localTimeToAmPmHour(localTime: LocalTime): Int {

    if (
        isBetween(
            localTime,
            LocalTime.of(0, 0),
            LocalTime.of(0, 59)
        )
    ) {
        return localTime.hour + 12
    }

    if (
        isBetween(
            localTime,
            LocalTime.of(1, 0),
            LocalTime.of(11, 59)
        )
    ) {
        return localTime.hour
    }

    if (
        isBetween(
            localTime,
            LocalTime.of(12, 0),
            LocalTime.of(12, 59)
        )
    ) {
        return localTime.hour
    }

    if (
        isBetween(
            localTime,
            LocalTime.of(13, 0),
            LocalTime.of(23, 59)
        )
    ) {
        return localTime.hour - 12
    }

    return localTime.hour
}

private fun isBetween(localTime: LocalTime, startTime: LocalTime, endTime: LocalTime): Boolean {
    return localTime in startTime..endTime
}

private fun amPmHourToHour24(amPmHour: Int, amPmMinute: Int, amPmValue: AmPmValue): Int {

    return when (amPmValue) {
        AmPmValue.AM -> {
            if (amPmHour == 12 && amPmMinute <= 59) {
                0
            } else {
                amPmHour
            }
        }

        AmPmValue.PM -> {
            if (amPmHour == 12 && amPmMinute <= 59) {
                amPmHour
            } else {
                amPmHour + 12
            }
        }
    }
}

private data class Minute(
    val text: String,
    val value: Int,
    val index: Int
)

private data class AmPm(
    val text: String,
    val value: AmPmValue,
    val index: Int?
)

internal enum class AmPmValue {
    AM, PM
}

private fun amPmValueFromTime(time: LocalTime): AmPmValue {
    return if (time.hour > 11) AmPmValue.PM else AmPmValue.AM
}











