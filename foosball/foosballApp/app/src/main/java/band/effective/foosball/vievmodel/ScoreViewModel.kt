package band.effective.foosball.vievmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import band.effective.foosball.arduinoconnection.UsbHandler
import band.effective.foosball.datamodelsupabase.GameScoreSupabase
import band.effective.foosball.network.UserRepository
import band.effective.foosball.presentation.components.routes.GameMode
import band.effective.foosball.roomdatabase.GameScore
import band.effective.foosball.roomdatabase.GameScoreRepository
import com.example.effectivefoosball.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.Realtime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScoreViewModel(private val repository: GameScoreRepository) : ViewModel() {

    private var leftScore = mutableStateOf(0)
    private var rightScore = mutableStateOf(0)
    private var usbHandler: UsbHandler? = null
    var startTime: Long = 0L
    val supabaseUrl = BuildConfig.SUPABASE_URL
    val supabaseKey = BuildConfig.SUPABASE_KEY

    val supabaseClient = createSupabaseClient(supabaseUrl, supabaseKey) {
        install(Realtime)
        install(Postgrest)
    }

    fun getLeftScore() = leftScore
    fun getRightScore() = rightScore

    var redTeamMember1 by mutableStateOf("")
        private set
    var redTeamMember2 by mutableStateOf("")
        private set
    var blueTeamMember1 by mutableStateOf("")
        private set
    var blueTeamMember2 by mutableStateOf("")
        private set

    fun updateRedTeam(member1: String, member2: String) {
        redTeamMember1 = member1
        redTeamMember2 = member2
    }

    fun updateBlueTeam(member1: String, member2: String) {
        blueTeamMember1 = member1
        blueTeamMember2 = member2
    }

    fun resetScore() {
        leftScore.value = 0
        rightScore.value = 0
    }

    fun startUsbConnection(context: Context) {
        usbHandler = UsbHandler { bytes ->
            processUsbData(bytes)
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    usbHandler?.setup(context)
                } catch (ex: Exception) {
                }
            }
        }
    }

    private fun processUsbData(bytes: ByteArray) {
        if (bytes.size == 1) {
            if (bytes[0].toInt() == 0) {
                leftScore.value += 1
            } else {
                rightScore.value += 1
            }
        }
    }

    fun saveGameScore(
        gameDate: String,
        redTeamMember1: String,
        redTeamMember2: String,
        blueTeamMember1: String,
        blueTeamMember2: String,
    ) {
        val gameScore = GameScore(
            gameDate = gameDate,
            redTeamMember1 = this.redTeamMember1,
            redTeamMember2 = this.redTeamMember2,
            blueTeamMember1 = this.blueTeamMember1,
            blueTeamMember2 = this.blueTeamMember2,
            scoreRed = leftScore.value,
            scoreBlue = rightScore.value,
        )

        viewModelScope.launch {
            repository.insertGameScore(gameScore)
        }
    }

    suspend fun sendGameScoreToSupabase(gameDate: String) {
        val gameScore = GameScoreSupabase(
            gameDate = gameDate,
            redTeamMember1 = redTeamMember1,
            redTeamMember2 = redTeamMember2,
            blueTeamMember1 = blueTeamMember1,
            blueTeamMember2 = blueTeamMember2,
            scoreRed = leftScore.value,
            scoreBlue = rightScore.value,
        )

        supabaseClient
            .from("game_scores")
            .insert(gameScore)
    }

    private val _currentGameMode = MutableStateFlow(GameMode.MAIN_MANU)
    val currentGameMode: StateFlow<GameMode> = _currentGameMode.asStateFlow()

    fun setGameMode(mode: GameMode) {
        _currentGameMode.value = mode
    }
}
