package band.effective.foosball.vievmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import band.effective.foosball.presentation.screens.tourGame.Game
import band.effective.foosball.presentation.screens.tourGame.generateGames

class GameOrderViewModel : ViewModel() {
    private val _games = mutableStateListOf<Game>()
    val games: List<Game> = _games

    init {
        _games.addAll(generateGames())
    }

    fun reorderGames(from: Int, to: Int) {
        _games.apply {
            add(to, removeAt(from))
        }
    }
}