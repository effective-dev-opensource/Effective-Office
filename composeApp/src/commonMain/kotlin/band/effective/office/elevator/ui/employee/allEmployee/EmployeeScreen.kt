package band.effective.office.elevator.ui.employee.allEmployee

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import band.effective.office.elevator.ExtendedThemeColors
import band.effective.office.elevator.MainRes
import band.effective.office.elevator.components.LoadingIndicator
import band.effective.office.elevator.components.generateImageLoader
import band.effective.office.elevator.textInBorderGray
import band.effective.office.elevator.theme_light_onPrimary
import band.effective.office.elevator.ui.employee.allEmployee.models.mappers.EmployeeCard
import band.effective.office.elevator.ui.employee.allEmployee.store.EmployeeStore
import band.effective.office.elevator.ui.main.SnackBarErrorMessage
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.rememberImagePainter
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.delay

@Composable
fun EmployeeScreen(component: EmployeeComponent) {

    val employState by component.employState.collectAsState()
    val employeesData = employState.changeShowedEmployeeCards
    val employeesCount = employState.countShowedEmployeeCards
    val userMessageState = employState.query

    var errorMessage by remember { mutableStateOf(MainRes.strings.something_went_wrong) }
    var isErrorMessageVisible by remember { mutableStateOf(false) }

    LaunchedEffect(component) {
        component.employLabel.collect { label ->
            when (label) {
                is EmployeeStore.Label.ShowProfileScreen -> component.onOutput(
                    EmployeeComponent.Output.OpenProfileScreen(
                        label.employee
                    )
                )
                is EmployeeStore.Label.ShowError -> {
                    errorMessage = label.message
                    isErrorMessageVisible = true
                    delay(3000)
                    isErrorMessageVisible = false
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        EmployeeScreenContent(
            isLoading = employState.isLoading,
            employeesData = employeesData,
            employeesCount = employeesCount,
            userMessageState = userMessageState,
            onCardClick = { component.onEvent(EmployeeStore.Intent.OnClickOnEmployee(it)) },
            onTextFieldUpdate = { component.onEvent(EmployeeStore.Intent.OnTextFieldUpdate(it)) },
        )
        SnackBarErrorMessage(
            message = stringResource(errorMessage),
            isVisible = isErrorMessageVisible,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun EmployeeScreenContent(
    employeesData: List<EmployeeCard>,
    employeesCount: String,
    userMessageState: String,
    onCardClick: (String) -> Unit,
    onTextFieldUpdate: (String) -> Unit,
    isLoading: Boolean
) {
    //TODO(Artem Gruzdev) need to fix this. We should to use snapshotFlow or derivedStateOf for showing
    // query result
    // see there: https://medium.com/androiddevelopers/effective-state-management-for-textfield-in-compose-d6e5b070fbe5
    var query by remember { mutableStateOf(userMessageState) }

    Column {
        Column(
            modifier = Modifier
                .background(theme_light_onPrimary)
                .padding(bottom = 15.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(MainRes.strings.employees),
                fontSize = 20.sp,
                fontWeight = FontWeight(600),//?
                color = ExtendedThemeColors.colors.blackColor,
                modifier = Modifier.padding(start = 20.dp, top = 55.dp, end = 15.dp, bottom = 25.dp)
            )
            TextField(
                value = query, onValueChange = {
                    query = it
                    onTextFieldUpdate(it)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 5.dp),
                textStyle = TextStyle(
                    color = ExtendedThemeColors.colors.trinidad_400,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(500)
                ),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = ExtendedThemeColors.colors.transparentColor,
                    disabledIndicatorColor = ExtendedThemeColors.colors.transparentColor,
                    unfocusedIndicatorColor = ExtendedThemeColors.colors.transparentColor,
                    backgroundColor = MaterialTheme.colors.background
                ),
                placeholder = {
                    Text(
                        text = stringResource(MainRes.strings.search_employee),
                        fontSize = 16.sp,
                        fontWeight = FontWeight(500),//Style. maththeme
                        color = textInBorderGray
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(MainRes.images.baseline_search_24),
                        contentDescription = "SearchField",
                        tint = textInBorderGray
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(32.dp)

            )

        }
        Column (
            modifier = Modifier
                .background(MaterialTheme.colors.onBackground)
                .fillMaxSize()
                .padding(start = 20.dp, top = 25.dp, end = 20.dp)
        ) {
            Row(modifier = Modifier.padding(bottom = 25.dp).fillMaxWidth()) {
                Text(
                    text = stringResource(MainRes.strings.employees) + " ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight(500),
                    color = ExtendedThemeColors.colors.blackColor
                )
                Text(
                    text = "($employeesCount)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight(400),
                    color = ExtendedThemeColors.colors.purple_heart_800
                )
            }

            when (isLoading) {
                true -> {
                    LoadingIndicator()
                }

                false -> {
                    LazyColumn {
                        items(employeesData) { employee_Data ->
                            EveryEmployeeCard(emp = employee_Data, onCardClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable

fun EveryEmployeeCard(emp: EmployeeCard, onCardClick: (String) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    if (isExpanded) {
        onCardClick(emp.id)
    }

    val imageLoader = generateImageLoader()

    Surface(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 15.dp)
            .animateContentSize()
            .clickable { isExpanded = !isExpanded },
        color = MaterialTheme.colors.onPrimary
    ) {
        Row(modifier = Modifier.padding(6.dp, 15.dp)) {
            emp.logoUrl.let { url ->
                val request = remember(url) {
                    ImageRequest {
                        data(url)
                    }
                }
                val painter = rememberImagePainter(
                    request = request,
                    imageLoader = imageLoader,
                    placeholderPainter = { painterResource(MainRes.images.logo_default) },
                    errorPainter = { painterResource(MainRes.images.logo_default) }
                )
                Image(
                    painter = painter,
                    contentDescription = "Employee logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(56.dp)
                )
            }

            Column(modifier = Modifier.padding(15.dp, 0.dp)) {
                Text(
                    text = emp.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(500),
                    color = ExtendedThemeColors.colors.blackColor
                )

                Spacer(modifier = Modifier.padding(0.dp, 4.dp))
                Text(
                    text = emp.post,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(400),
                    color = textInBorderGray
                )
            }
        }
    }
}

