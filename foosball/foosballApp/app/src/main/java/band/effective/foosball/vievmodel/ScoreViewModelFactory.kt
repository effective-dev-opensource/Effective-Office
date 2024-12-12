package band.effective.foosball.vievmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import band.effective.foosball.roomdatabase.GameScoreRepository

class ScoreViewModelFactory(private val repository: GameScoreRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
