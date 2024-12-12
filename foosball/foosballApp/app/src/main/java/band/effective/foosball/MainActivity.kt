package band.effective.foosball

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import band.effective.foosball.network.UserViewModel
import band.effective.foosball.presentation.components.MainScreen
import band.effective.foosball.presentation.screens.onboarding.OnBoardingScreen
import band.effective.foosball.roomdatabase.GameScoreDatabase
import band.effective.foosball.roomdatabase.GameScoreRepositoryImpl
import band.effective.foosball.ui.theme.EffectiveFoosballTheme
import band.effective.foosball.vievmodel.ScoreViewModel
import band.effective.foosball.vievmodel.ScoreViewModelFactory

class MainActivity : ComponentActivity() {

    private val gameScoreDao by lazy { GameScoreDatabase.getDatabase(this).gameScoreDao() }
    private val scoreViewModel: ScoreViewModel by viewModels {
        ScoreViewModelFactory(GameScoreRepositoryImpl(gameScoreDao))
    }
   private  val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        scoreViewModel.startUsbConnection(this)
        setContent {
            EffectiveFoosballTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isScreenVisible by remember { mutableStateOf(true) }

                    if (isScreenVisible) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = { isScreenVisible = false }
                                    )
                                }
                        ) {
                            OnBoardingScreen()
                        }
                    } else {
                        MainScreen(
                            scoreViewModel = scoreViewModel,
                            userViewModel= userViewModel
                        )
                    }
                }
            }
        }
    }
}

