package band.effective.foosball.presentation.components

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import band.effective.foosball.network.UserViewModel
import band.effective.foosball.presentation.components.routes.Constants
import band.effective.foosball.presentation.components.routes.GameMode
import band.effective.foosball.presentation.components.routes.Routes
import band.effective.foosball.presentation.screens.competitionGame.CreateTeamFastGame
import band.effective.foosball.presentation.screens.competitionGame.ScoreScreenComp
import band.effective.foosball.presentation.screens.competitionGame.TeamsShowScreen
import band.effective.foosball.presentation.screens.competitionGame.WinnerScreenComp
import band.effective.foosball.presentation.screens.competitionGame.competitionDialog.CompNewGameDialog
import band.effective.foosball.presentation.screens.dialogs.EndGame
import band.effective.foosball.presentation.screens.dialogs.GameIsFinished
import band.effective.foosball.presentation.screens.fastGame.ScoreScreen
import band.effective.foosball.presentation.screens.fastGame.StartGameScreen
import band.effective.foosball.presentation.screens.fastGame.fastDialog.NewGameDialog
import band.effective.foosball.presentation.screens.onboarding.MainMenu
import band.effective.foosball.presentation.screens.tourGame.GameOrderScreen
import band.effective.foosball.presentation.screens.tourGame.OrderOfGamesScreen
import band.effective.foosball.presentation.screens.tourGame.TeamDistributionScreen
import band.effective.foosball.presentation.screens.tourGame.TourStartScreen
import band.effective.foosball.vievmodel.GameOrderViewModel
import band.effective.foosball.vievmodel.ScoreViewModel

@Composable
fun MainScreen(scoreViewModel: ScoreViewModel,userViewModel: UserViewModel) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val navController = rememberNavController()

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.MAIN_MENU,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Routes.MAIN_MENU) { MainMenu(navController) }
                composable(Routes.START_GAME_SCREEN) {
                    ScreenWithNavigation(navController) {
                        scoreViewModel.setGameMode(GameMode.FAST)
                        scoreViewModel.resetScore()
                        StartGameScreen(navController)
                    }
                }
                composable(Routes.TEAM_FAST_GAME) {
                    ScreenWithNavigation(navController) {
                        scoreViewModel.setGameMode(GameMode.COMPETITIVE)
                        CreateTeamFastGame(navController, scoreViewModel, userViewModel )
                    }
                }
                composable(
                    "displayScreen/{redTeamMember1}/{redTeamMember2}/{blueTeamMember1}/{blueTeamMember2}",
                    arguments = listOf(
                        navArgument("redTeamMember1") { type = NavType.StringType },
                        navArgument("redTeamMember2") { type = NavType.StringType },
                        navArgument("blueTeamMember1") { type = NavType.StringType },
                        navArgument("blueTeamMember2") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val redTeamMember1 = backStackEntry.arguments?.getString("redTeamMember1") ?: ""
                    val redTeamMember2 = backStackEntry.arguments?.getString("redTeamMember2") ?: ""
                    val blueTeamMember1 =
                        backStackEntry.arguments?.getString("blueTeamMember1") ?: ""
                    val blueTeamMember2 =
                        backStackEntry.arguments?.getString("blueTeamMember2") ?: ""
                    TeamsShowScreen(
                        redTeamMember1 = redTeamMember1,
                        redTeamMember2 = redTeamMember2,
                        blueTeamMember1 = blueTeamMember1,
                        blueTeamMember2 = blueTeamMember2,
                        navController
                    )
                }

                composable(
                    "${Routes.SCORE_SCREEN_COMP}/{redTeamMember1}/{redTeamMember2}/{blueTeamMember1}/{blueTeamMember2}",
                    arguments = listOf(
                        navArgument("redTeamMember1") { type = NavType.StringType },
                        navArgument("redTeamMember2") { type = NavType.StringType },
                        navArgument("blueTeamMember1") { type = NavType.StringType },
                        navArgument("blueTeamMember2") { type = NavType.StringType }
                    )
                ) {
                    backStackEntry ->
                    val redTeamMember1 = backStackEntry.arguments?.getString("redTeamMember1") ?: ""
                    val redTeamMember2 = backStackEntry.arguments?.getString("redTeamMember2") ?: ""
                    val blueTeamMember1 =
                        backStackEntry.arguments?.getString("blueTeamMember1") ?: ""
                    val blueTeamMember2 =
                        backStackEntry.arguments?.getString("blueTeamMember2") ?: ""
                    ScreenWithNavigation(navController) {
                        ScoreScreenComp(
                            scoreViewModel = scoreViewModel,
                            redTeamMember1 = redTeamMember1,
                            redTeamMember2 = redTeamMember2,
                            blueTeamMember1 = blueTeamMember1,
                            blueTeamMember2 = blueTeamMember2,
                            navController = navController
                        )
                    }
                }

                composable(Routes.GAME_IS_FINISHED) { GameIsFinished(navController) }
                composable(Routes.SCORE_SCREEN) {
                    ScreenWithNavigation(navController) {
                        ScoreScreen(scoreViewModel)
                    }
                }
                composable(Routes.ALERT_DIALOG) { AlertDialog() }
                composable(Routes.SELECT) { EndGame(navController, scoreViewModel) }
                composable(Routes.HOME) { MainMenu(navController) }
                composable(Routes.BACK) { onBackPressedDispatcher?.onBackPressed() }
                composable(Routes.PLAY) { ScoreScreen(scoreViewModel) }
                composable(Routes.MUTE) { ScoreScreen(scoreViewModel) }
                composable(Routes.NEW_FAST_GAME_DIALOG) { NewGameDialog(navController) }
                composable(Routes.NEW_COMP_GAME_DIALOG) { CompNewGameDialog(navController) }
                //composable(Routes.NEW_TOUR_GAME_DIALOG) { TourNewGameDialog(navController) }
                composable(Routes.WINNER_SCREEN_COMP) {
                    val redTeamScore = scoreViewModel.getLeftScore().value
                    val blueTeamScore = scoreViewModel.getRightScore().value
                    val redTeamMember1 = scoreViewModel.redTeamMember1
                    val redTeamMember2 = scoreViewModel.redTeamMember2
                    val blueTeamMember1 = scoreViewModel.blueTeamMember1
                    val blueTeamMember2 = scoreViewModel.blueTeamMember2

                    WinnerScreenComp(
                        navController = navController,
                        redTeamMember1 = redTeamMember1,
                        redTeamMember2 = redTeamMember2,
                        blueTeamMember1 = blueTeamMember1,
                        blueTeamMember2 = blueTeamMember2,
                        redTeamScore = redTeamScore,
                        blueTeamScore = blueTeamScore,
                        scoreViewModel = scoreViewModel
                    )
                }

                composable(Routes.TOUR_START_SCREEN) {
                    ScreenWithNavigation(navController) {
                        TourStartScreen(navController)
                    }
                }
                composable(Routes.TEAM_DRAG_DROP_LIST_SCREEN) {
                    ScreenWithNavigation(navController) {
                        val gameOrderViewModel: GameOrderViewModel = viewModel()
                        GameOrderScreen(
                            viewModel = gameOrderViewModel,
                            navController = navController
                        )
                    }
                }
                composable(
                    "${Routes.TEAM_DISTRIBUTION_SCREEN}/{teamCount}",
                    arguments = listOf(navArgument("teamCount") { type = NavType.IntType })
                ) {
                    backStackEntry ->
                    val teamCount = backStackEntry.arguments?.getInt("teamCount")
                        ?: Constants.MIN_LENGTH_COUNTER_TEAM
                    TeamDistributionScreen(teamCount = teamCount, navController)
                }
                composable(
                    "game1/{team1}/{team2}",
                    arguments = listOf(
                        navArgument("team1") { type = NavType.StringType },
                        navArgument("team2") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val team1 =
                        backStackEntry.arguments?.getString("team1")?.split(",") ?: emptyList()
                    val team2 =
                        backStackEntry.arguments?.getString("team2")?.split(",") ?: emptyList()
                    OrderOfGamesScreen(team1 = team1, team2 = team2)
                }
            }
        }
    }
}
