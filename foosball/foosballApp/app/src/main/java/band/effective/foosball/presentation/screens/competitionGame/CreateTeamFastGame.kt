package band.effective.foosball.presentation.screens.competitionGame

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import band.effective.foosball.network.UserViewModel
import band.effective.foosball.presentation.components.CustomButton
import band.effective.foosball.ui.theme.Blue
import band.effective.foosball.ui.theme.DrukWide
import band.effective.foosball.ui.theme.Orange
import band.effective.foosball.ui.theme.Red
import band.effective.foosball.ui.theme.Roboto
import band.effective.foosball.ui.theme.SuperLightGray
import band.effective.foosball.ui.theme.TeamList
import band.effective.foosball.ui.theme.White
import band.effective.foosball.vievmodel.ScoreViewModel
import com.example.effectivefoosball.R



@Composable
fun CreateTeamFastGame(navController: NavController, scoreViewModel: ScoreViewModel, userViewModel: UserViewModel) {
    var redTeamMember1 by remember { mutableStateOf("") }
    var redTeamMember2 by remember { mutableStateOf("") }
    var blueTeamMember1 by remember { mutableStateOf("") }
    var blueTeamMember2 by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.teams),
                style = TextStyle(
                    color = White,
                    fontSize = 45.sp,
                    fontFamily = DrukWide
                ),
                modifier = Modifier.padding(bottom = 32.dp)
            )
            TeamInputField(
                teamName = stringResource(id = R.string.team_red),
                member1 = redTeamMember1,
                member2 = redTeamMember2,
                onMember1Change = { value ->
                    redTeamMember1 = value
                    scoreViewModel.updateRedTeam(value, redTeamMember2)
                },
                onMember2Change = { value ->
                    redTeamMember2 = value
                    scoreViewModel.updateRedTeam(redTeamMember1, value)
                },
                borderColor = Red,
                userViewModel = userViewModel
            )
            TeamInputField(
                teamName = stringResource(id = R.string.team_blue),
                member1 = blueTeamMember1,
                member2 = blueTeamMember2,
                onMember1Change = { value ->
                    blueTeamMember1 = value
                    scoreViewModel.updateBlueTeam(value, blueTeamMember2)
                },
                onMember2Change = { value ->
                    blueTeamMember2 = value
                    scoreViewModel.updateBlueTeam(blueTeamMember1, value)
                },
                borderColor = Blue,
                userViewModel = userViewModel
            )
            CustomButton(
                defaultColor = Orange,
                text = stringResource(id = R.string.next),
                onClick = {
                    if (redTeamMember1.isNotBlank() &&
                        redTeamMember2.isNotBlank() &&
                        blueTeamMember1.isNotBlank() &&
                        blueTeamMember2.isNotBlank()
                    ) {
                        navController.navigate(
                            "displayScreen/$redTeamMember1/$redTeamMember2/$blueTeamMember1/$blueTeamMember2"
                        )
                    } else {
                        showDialog = true
                    }
                },
                width = 340.dp,
                height = 98.dp,
                border = BorderStroke(0.dp, Orange),
                cornerRadius = 80.dp
            )
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                        Text(
                            text = stringResource(id = R.string.error),
                            style = TextStyle(
                                color = White,
                                fontSize = 20.sp,
                                fontFamily = DrukWide
                            )
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.fill_all_fields),
                            style = TextStyle(
                                color = White,
                                fontSize = 23.sp,
                                fontFamily = Roboto
                            )
                        )
                    },
                    confirmButton = {
                        CustomButton(
                            defaultColor = Orange,
                            text = stringResource(id = R.string.ok),
                            onClick = { showDialog = false },
                            width = 100.dp,
                            height = 50.dp,
                            border = BorderStroke(0.dp, Orange)
                        )
                    },
                    containerColor = TeamList,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

@Composable
fun TeamInputField(
    userViewModel: UserViewModel,
    teamName: String,
    member1: String,
    member2: String,
    onMember1Change: (String) -> Unit,
    onMember2Change: (String) -> Unit,
    borderColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = teamName,
            style = TextStyle(
                color = White,
                fontSize = 40.sp,
                fontFamily = Roboto
            ),
            modifier = Modifier.width(200.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerInputWithDropdown(
                value = member1,
                onValueChange = onMember1Change,
                borderColor = borderColor,
                userViewModel = userViewModel
            )
            Text(
                "/",
                color = White,
                fontSize = 40.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            PlayerInputWithDropdown(
                value = member2,
                onValueChange = onMember2Change,
                borderColor = borderColor,
                userViewModel = userViewModel
            )
        }
    }
}

@Composable
fun PlayerInputWithDropdown(
    userViewModel: UserViewModel = viewModel(),
    value: String,
    onValueChange: (String) -> Unit,
    borderColor: Color
) {
    LaunchedEffect(Unit) {
        userViewModel.loadUserNames()
    }

    val userNames by userViewModel.userNames.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    // Добавляем анимацию для поворота стрелки
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    Column(modifier = Modifier.width(390.dp)) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .border(
                    width = 1.8.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(30.dp)
                )
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
            shape = RoundedCornerShape(30.dp),
            value = value,
            onValueChange = {
                //  Log.d("PlayerInputWithDropdown", "Value changed: $it")
                onValueChange(it)
                expanded = true
            },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.player),
                    style = TextStyle(
                        color = SuperLightGray,
                        fontSize = 30.sp,
                        fontFamily = Roboto
                    )
                )
            },
            colors = TextFieldDefaults.colors(
                disabledContainerColor = TeamList,
                focusedContainerColor = TeamList,
                unfocusedContainerColor = TeamList
            ),
            textStyle = TextStyle(
                color = White,
                fontSize = 30.sp,
                fontFamily = Roboto
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(rotationState), // Применяем анимацию поворота
                        painter = painterResource(id = R.drawable.ic_unwarp),
                        contentDescription = null,
                        tint = SuperLightGray
                    )
                }
            }
        )
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .width(390.dp)
                    .heightIn(max = 250.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TeamList)
            ) {
                LazyColumn {
                    val filteredPlayers = if (value.isNotEmpty()) {
                        userNames.filter { it.lowercase().contains(value.lowercase()) }
                    } else {
                        userNames
                    }
                    items(filteredPlayers) { player ->
                        ItemsCategory(title = player) { selectedPlayer ->
                            onValueChange(selectedPlayer)
                            expanded = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemsCategory(
    title: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(title) }
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 30.sp,
            color = White,
            fontFamily = Roboto
        )
    }
}

