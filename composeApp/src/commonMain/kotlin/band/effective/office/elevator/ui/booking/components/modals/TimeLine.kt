package band.effective.office.elevator.ui.booking.components.modals

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import band.effective.office.elevator.MainRes
import band.effective.office.elevator.common.compose.components.GrayText
import band.effective.office.elevator.utils.convertDateTimeToUiDateString
import band.effective.office.elevator.utils.convertTimeToString
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Composable
fun TimeLine(
    startDate: LocalDate,
    endDate: LocalDate,
    startTime: LocalTime,
    endTime: LocalTime,
    selectTimeActive: Boolean,
    onPickDate: () -> Unit,
    onPickStartTime: () -> Unit,
    onPickEndTime: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
    ) {
        when (startDate == endDate) {
            true -> SingleTimeLine(
                startDate = startDate,
                startTime = startTime,
                endTime = endTime,
                selectTimeActive = selectTimeActive,
                onPickDate = onPickDate,
                onPickStartTime = onPickStartTime,
                onPickEndTime = onPickEndTime
            )

            false -> NonSingleTimeLine(
                startDate = startDate,
                startTime = startTime,
                endDate = endDate,
                endTime = endTime,
                selectTimeActive = selectTimeActive,
                onPickDate = onPickDate,
                onPickStartTime = onPickStartTime,
                onPickEndTime = onPickEndTime
            )
        }
    }
}

@Composable
private fun SingleTimeLine(
    startDate: LocalDate,
    startTime: LocalTime,
    endTime: LocalTime,
    selectTimeActive: Boolean,
    onPickDate: () -> Unit,
    onPickStartTime: () -> Unit,
    onPickEndTime: () -> Unit,
) {
    Text(
        text = convertDateTimeToUiDateString(startDate),
        modifier = Modifier.clickable { onPickDate() },
        style = MaterialTheme.typography.button.copy(
            fontWeight = FontWeight(
                weight = 400
            ),
            color = Color.Black
        )
    )

    Spacer(modifier = Modifier.width(24.dp))

    if (selectTimeActive) {
        Text(
            text = stringResource(MainRes.strings.from_date, convertTimeToString(startTime)),
            modifier = Modifier.clickable { if (selectTimeActive) onPickStartTime() },
            style = MaterialTheme.typography.button.copy(
                fontWeight = FontWeight(
                    weight = 400
                ),
                color = Color.Black
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(MainRes.strings.to_date, convertTimeToString(endTime)),
            modifier = Modifier.clickable { if (selectTimeActive) onPickEndTime() },
            style = MaterialTheme.typography.button.copy(
                fontWeight = FontWeight(
                    weight = 400
                ),
                color = Color.Black
            )
        )
    } else {
        GrayText(
            text = stringResource(MainRes.strings.from_date, convertTimeToString(startTime)),
            style = MaterialTheme.typography.button.copy(
                fontWeight = FontWeight(
                    weight = 400
                ),
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        GrayText(
            text = stringResource(MainRes.strings.to_date, convertTimeToString(endTime)),
            style = MaterialTheme.typography.button.copy(
                fontWeight = FontWeight(
                    weight = 400
                ),
            )
        )
    }
}

@Composable
private fun NonSingleTimeLine(
    startDate: LocalDate,
    startTime: LocalTime,
    endDate: LocalDate,
    endTime: LocalTime,
    selectTimeActive: Boolean,
    onPickDate: () -> Unit,
    onPickStartTime: () -> Unit,
    onPickEndTime: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = convertDateTimeToUiDateString(startDate, endDate),
            modifier = Modifier.clickable { onPickDate() },
            style = MaterialTheme.typography.button.copy(
                fontWeight = FontWeight(
                    weight = 400
                ),
                color = Color.Black
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row {
            if (selectTimeActive) {
                Text(
                    text = stringResource(MainRes.strings.from_date, convertTimeToString(startTime)),
                    modifier = Modifier.clickable { if (selectTimeActive) onPickStartTime() },
                    style = MaterialTheme.typography.button.copy(
                        fontWeight = FontWeight(
                            weight = 400
                        ),
                        color = Color.Black
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(MainRes.strings.to_date, convertTimeToString(endTime)),
                    modifier = Modifier.clickable { if (selectTimeActive) onPickEndTime() },
                    style = MaterialTheme.typography.button.copy(
                        fontWeight = FontWeight(
                            weight = 400
                        ),
                        color = Color.Black
                    )
                )
            } else {
                GrayText(
                    text = stringResource(MainRes.strings.from_date, convertTimeToString(startTime)),
                    style = MaterialTheme.typography.button.copy(
                        fontWeight = FontWeight(
                            weight = 400
                        ),
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                GrayText(
                    text = stringResource(MainRes.strings.to_date, convertTimeToString(endTime)),
                    style = MaterialTheme.typography.button.copy(
                        fontWeight = FontWeight(
                            weight = 400
                        ),
                    )
                )
            }
        }
    }
}



