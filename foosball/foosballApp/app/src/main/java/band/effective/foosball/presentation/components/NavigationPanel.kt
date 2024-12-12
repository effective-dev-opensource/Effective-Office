package band.effective.foosball.presentation.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import band.effective.foosball.presentation.components.routes.Routes
import band.effective.foosball.ui.theme.BackgroundColor
import band.effective.foosball.ui.theme.Orange
import band.effective.foosball.ui.theme.Typography
import band.effective.foosball.ui.theme.White
import com.example.effectivefoosball.R

val screensWithoutSelect = listOf(
    Routes.MAIN_MENU,
    Routes.START_GAME_SCREEN,
    Routes.TOUR_START_SCREEN,
    Routes.TEAM_DISTRIBUTION_SCREEN,
    Routes.TEAM_FAST_GAME,
    Routes.TEAM_DISTRIBUTION_SCREEN,
    Routes.TEAM_DRAG_DROP_LIST_SCREEN
)

@Composable
fun NavigationPanel(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showSelect = currentRoute != null && currentRoute !in screensWithoutSelect
    val selectVisible = remember { mutableStateOf(showSelect) }

    LaunchedEffect(showSelect) {
        selectVisible.value = showSelect
    }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(IntrinsicSize.Min)
            .background(BackgroundColor)
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = selectVisible.value,
                enter = slideInHorizontally(initialOffsetX = { it }) + expandVertically(expandFrom = Alignment.Top),
                exit = slideOutHorizontally(targetOffsetX = { it }) + shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                NavigationItem(
                    icon = R.drawable.ic_select,
                    label = R.string.finalize,
                    onClick = { navController.navigate(Routes.SELECT) },
                    isSelected = currentRoute == Routes.SELECT
                )
            }

            NavigationItem(
                icon = R.drawable.ic_home,
                label = R.string.main,
                onClick = { navController.navigate(Routes.HOME) },
                isSelected = currentRoute == Routes.HOME
            )

            NavigationItem(
                icon = R.drawable.ic_back,
                label = R.string.back,
                onClick = { navController.navigate(Routes.BACK) },
                isSelected = currentRoute == Routes.BACK
            )

            NavigationItem(
                icon = R.drawable.ic_play,
                label = R.string.recording,
                onClick = { navController.navigate(Routes.ALERT_DIALOG) },
                isSelected = currentRoute == Routes.PLAY
            )

            NavigationItem(
                icon = R.drawable.ic_notstream,
                label = R.string.stream,
                onClick = { Routes.ALERT_DIALOG },
                isSelected = currentRoute == Routes.MUTE
            )
        }
    }
}

@Composable
fun NavigationItem(
    @DrawableRes icon: Int,
    @StringRes label: Int,
    onClick: () -> Unit,
    isSelected: Boolean
) {
    val iconColor = if (isSelected) Orange else White
    val textColor = if (isSelected) Orange else White

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = iconColor
            )
        }
        Text(
            text = stringResource(label),
            style = Typography.labelSmall,
            color = textColor
        )
    }
}

@Preview()
@Composable
fun Preview() {
    NavigationPanel(navController = rememberNavController())
}
