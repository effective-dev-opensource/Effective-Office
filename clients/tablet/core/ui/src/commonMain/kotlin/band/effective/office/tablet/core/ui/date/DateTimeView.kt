package band.effective.office.tablet.core.ui.date

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import band.effective.office.shared.core.utils.dateTimeFormat
import band.effective.office.shared.core.utils.dayMonthFormat
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.arrow_left
import band.effective.office.tablet.core.ui.arrow_right
import band.effective.office.tablet.core.ui.select_date_tine_title
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h6
import band.effective.office.tablet.core.ui.theme.h8
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDateTime
import band.effective.office.tablet.core.ui.res.painterResource
import org.jetbrains.compose.resources.stringResource
import band.effective.office.tablet.core.ui.utils.DateDisplayMapper

@OptIn(ExperimentalTime::class)
@Composable
fun DateTimeView(
    modifier: Modifier,
    selectDate: LocalDateTime,
    currentDate: LocalDateTime,
    increment: () -> Unit,
    decrement: () -> Unit,
    onOpenDateTimePickerModal: () -> Unit,
    showTitle: Boolean = false
) {
    Column(modifier = modifier) {
        if (showTitle) {
            DateTimeTitle()
            Spacer(modifier = Modifier.height(10.dp))
        }
        Row {
            PreviousDateButton(decrement)
            Spacer(modifier = Modifier.width(10.dp))
            SelectedDate(onOpenDateTimePickerModal, selectDate, currentDate)
            Spacer(modifier = Modifier.width(10.dp))
            NextDateButton(increment)
        }
    }
}

@Composable
private fun RowScope.NextDateButton(increment: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxHeight().weight(1f).clip(RoundedCornerShape(15.dp)),
        onClick = { increment() },
        colors = ButtonDefaults.buttonColors(
            containerColor = LocalCustomColorsPalette.current.elevationBackground
        )
    ) {
        Image(
            modifier = Modifier,
            painter = painterResource(Res.drawable.arrow_right),
            contentDescription = null
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun RowScope.SelectedDate(
    onOpenDateTimePickerModal: () -> Unit,
    selectDate: LocalDateTime,
    currentDate: LocalDateTime
) {
    val timeDayMonthDateFormat = remember { dateTimeFormat }
    val dayMonthDateFormat = remember { dayMonthFormat }
    var previousDate by remember { mutableStateOf(selectDate) }

    val slideDirection = remember(selectDate) {
        if (selectDate > previousDate) AnimatedContentTransitionScope.SlideDirection.Left
        else AnimatedContentTransitionScope.SlideDirection.Right
    }

    LaunchedEffect(selectDate) {
        previousDate = selectDate
    }

    if (currentDate.date < selectDate.date) {
        Button(
            modifier = Modifier
                .fillMaxHeight()
                .weight(4f)
                .clip(RoundedCornerShape(15.dp)),
            onClick = onOpenDateTimePickerModal,
            colors = ButtonDefaults.buttonColors(
                containerColor = LocalCustomColorsPalette.current.elevationBackground
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            AnimatedContent(
                targetState = DateDisplayMapper.map(
                    selectDate = selectDate,
                    currentDate = currentDate
                ),
                transitionSpec = {
                    slideIntoContainer(slideDirection) + fadeIn() with
                            slideOutOfContainer(slideDirection.opposite()) + fadeOut()
                },
                label = "AnimatedDateChange"
            ) { formattedDate ->
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.h6
                )
            }
        }
    } else {
        Button(
            modifier = Modifier
                .fillMaxHeight()
                .weight(4f)
                .clip(RoundedCornerShape(15.dp)),
            onClick = onOpenDateTimePickerModal,
            colors = ButtonDefaults.buttonColors(
                containerColor = LocalCustomColorsPalette.current.elevationBackground
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            AnimatedContent(
                targetState = DateDisplayMapper.map(
                    selectDate = currentDate,
                    currentDate = currentDate
                ),
                transitionSpec = {
                    slideIntoContainer(slideDirection) + fadeIn() with
                            slideOutOfContainer(slideDirection.opposite()) + fadeOut()
                },
                label = "AnimatedDateChange"
            ) { formattedDate ->
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.h6
                )
            }
        }
    }
}

// Удлинение SlideDirection для удобства
private fun AnimatedContentTransitionScope.SlideDirection.opposite(): AnimatedContentTransitionScope.SlideDirection {
    return when (this) {
        AnimatedContentTransitionScope.SlideDirection.Left -> AnimatedContentTransitionScope.SlideDirection.Right
        AnimatedContentTransitionScope.SlideDirection.Right -> AnimatedContentTransitionScope.SlideDirection.Left
        else -> this
    }
}

@Composable
private fun RowScope.PreviousDateButton(decrement: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxHeight().weight(1f).clip(RoundedCornerShape(15.dp)),
        onClick = { decrement() },
        colors = ButtonDefaults.buttonColors(
            containerColor = LocalCustomColorsPalette.current.elevationBackground
        )
    ) {
        Image(
            modifier = Modifier,
            painter = painterResource(Res.drawable.arrow_left),
            contentDescription = null
        )
    }
}

@Composable
private fun DateTimeTitle() {
    Text(
        text = stringResource(Res.string.select_date_tine_title),
        color = LocalCustomColorsPalette.current.secondaryTextAndIcon,
        style = MaterialTheme.typography.h8
    )
}
