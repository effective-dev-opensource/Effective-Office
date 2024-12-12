package band.effective.foosball.network

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _userNames = MutableStateFlow<List<String>>(emptyList())
    val userNames: StateFlow<List<String>> = _userNames

    private var dataLoaded = false

    init {
        // Загружаем данные при создании ViewModel, если они еще не загружены
        if (!dataLoaded) {
            loadUserNames()
        }
    }

    fun loadUserNames(tag: String? = null, email: String? = null) {
        if (dataLoaded) return // Предотвращаем повторные запросы

        viewModelScope.launch {
            try {
                val names = UserRepository.fetchUserNames(tag, email)
                _userNames.value = names
                dataLoaded = true // Обозначаем, что данные уже загружены
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching user names", e)
            }
        }
    }
}
