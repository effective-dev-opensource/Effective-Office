package band.effective.office.tv.screen.eventStory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import band.effective.office.tv.core.network.Either
import band.effective.office.tv.core.ui.screen_with_controls.TimerSlideShow
import band.effective.office.tv.domain.model.clockify.ClockifyUser
import band.effective.office.tv.domain.model.duolingo.DuolingoUser
import band.effective.office.tv.domain.model.message.BotMessage
import band.effective.office.tv.domain.model.message.MessageQueue
import band.effective.office.tv.domain.model.notion.EmployeeInfoEntity
import band.effective.office.tv.domain.model.notion.processEmployeeInfo
import band.effective.office.tv.domain.use_cases.EventStoryDataCombinerUseCase
import band.effective.office.tv.network.MattermostClient
import band.effective.office.tv.repository.supernova.Talent
import band.effective.office.tv.repository.supernova.toUi
import band.effective.office.tv.screen.autoplayController.AutoplayController
import band.effective.office.tv.screen.autoplayController.model.AutoplayState
import band.effective.office.tv.screen.autoplayController.model.OnSwitchCallbacks
import band.effective.office.tv.screen.autoplayController.model.ScreenState
import band.effective.office.tv.screen.duolingo.model.toUI
import band.effective.office.tv.screen.eventStory.models.DuolingoUserInfo
import band.effective.office.tv.screen.eventStory.models.MessageInfo
import band.effective.office.tv.screen.eventStory.models.SportUserInfo
import band.effective.office.tv.screen.eventStory.models.StoryModel
import band.effective.office.tv.screen.eventStory.models.SupernovaUserInfo
import band.effective.office.tv.screen.navigation.Screen
import band.effective.office.tv.screen.ratings.sport.model.toUi
import band.effective.office.tv.utils.StringResource
import coil.ImageLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EventStoryViewModel @Inject constructor(
    private val eventStoryData: EventStoryDataCombinerUseCase,
    private val timer: TimerSlideShow,
    @MattermostClient val imageLoader: ImageLoader,
    private val autoplayController: AutoplayController
) : ViewModel() {
    private val mutableState = MutableStateFlow(LatestEventInfoUiState.empty)
    val state = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            initDataStory()
        }
        initTimer()
        checkMessage()

        autoplayController.addCallbacks(Screen.Stories, object : OnSwitchCallbacks {
            override fun onForwardSwitch(controllerState: AutoplayState) {
                mutableState.update {
                    it.copy(
                        isPlay = autoplayController.state.value.screenState.isPlay,
                        currentStoryIndex = 0
                    )
                }
                if (autoplayController.state.value.screenState.isPlay) timer.startTimer()
            }

            override fun onBackSwitch(controllerState: AutoplayState) {
                mutableState.update {
                    it.copy(
                        isPlay = autoplayController.state.value.screenState.isPlay,
                        currentStoryIndex = it.eventsInfo.size - 1
                    )
                }
                if (autoplayController.state.value.screenState.isPlay) timer.startTimer()
            }

            override fun onLeave() {
                timer.stopTimer()
            }
        })
    }

    private fun checkMessage() = viewModelScope.launch {

        MessageQueue.secondQueue.queue.collect {
            val messages = MessageQueue.secondQueue.queue.value.queue
            val messagesInStory =
                state.value.eventsInfo.filterIsInstance<MessageInfo>().map { it.message }
            val commonMessages =
                messagesInStory.filter { messagesInStory -> messages.any { messageInQueue -> messageInQueue.id == messagesInStory.id } }
            val addMessages = (messages - commonMessages.toSet()).map { it.toMessageInfo() }
            val deleteMessages =
                (messagesInStory - commonMessages.toSet()).map { it.toMessageInfo() }
            val newEventInfo = state.value.eventsInfo + addMessages - deleteMessages.toSet()
            mutableState.update {
                it.copy(
                    eventsInfo = newEventInfo,
                    currentStoryIndex = if (it.currentStoryIndex >= newEventInfo.size)
                        newEventInfo.size - 1
                    else it.currentStoryIndex
                )
            }
        }
    }

    private suspend fun initDataStory() {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            updateStateAsException(
                StringResource.DynamicResource((exception as Error).message.orEmpty())
            )
        }
        withContext(Dispatchers.IO + exceptionHandler) {
            eventStoryData.getAllDataForStories().collectLatest { events ->
                when (events) {
                    is Either.Success -> {
                        val teammates = events.data.filterIsInstance<EmployeeInfoEntity>()
                        val employeeStories = teammates.processEmployeeInfo()
                        updateStateAsSuccessfulFetch(
                            employeeStories +
                                    setDuolingoDataToStoryModel(
                                        events.data.filterIsInstance<DuolingoUser>()
                                    ) +
                                    setClockifyDataToStoryModel(
                                        events.data.filterIsInstance<ClockifyUser>(),
                                        teammates
                                    ) +
                                    setSupernovaDataToStoryModel(
                                        events.data.filterIsInstance<Talent>(),
                                        teammates
                                    )
                        )
                    }
                    is Either.Failure -> updateStateAsException(events.error)
                }
            }
        }
        startTimer()
    }

    private fun setClockifyDataToStoryModel(
        clockifyUsers: List<ClockifyUser>,
        employees: List<EmployeeInfoEntity>
    ) =
        SportUserInfo(
            users = clockifyUsers
                .toUi(employees)
                .take(countUsersToShow)
                .sortedByDescending { it.totalSeconds }
        ) as StoryModel

    private fun setSupernovaDataToStoryModel(
        supernovaUsers: List<Talent>,
        employees: List<EmployeeInfoEntity>
    ) =
        SupernovaUserInfo(
            users = supernovaUsers
                .toUi(employees)
                .sortedByDescending { it.score }
                .take(countUsersToShow)
        ) as StoryModel

    private fun setDuolingoDataToStoryModel(duolingoUsers: List<DuolingoUser>) =
        run {
            val userXpSort = DuolingoUserInfo(
                users = duolingoUsers
                    .sortedByDescending { it.totalXp }
                    .take(countUsersToShow)
                    .toUI(),
                keySort = KeySortDuolingoUser.Xp
            ) as StoryModel
            val userStreakSort = DuolingoUserInfo(
                users = duolingoUsers
                    .filter { it.streakDay != 0 }
                    .take(countUsersToShow)
                    .sortedByDescending { it.streakDay }
                    .toUI(),
                keySort = KeySortDuolingoUser.Streak
            ) as StoryModel
            listOf(userXpSort, userStreakSort)
        }

    private fun updateStateAsException(error: StringResource) {
        autoplayController.addError(
            screen = Screen.Stories,
            errorText = error
        )
        mutableState.update { state ->
            state.copy(
                isError = true,
                errorText = error,
                isLoading = false,
            )
        }
    }

    private fun updateStateAsSuccessfulFetch(events: List<StoryModel>) {
        mutableState.update { state ->
            state.copy(
                eventsInfo = events,
                currentStoryIndex = if (autoplayController.state.value.screenState.isForwardDirection) 0 else events.size - 1,
                isLoading = false,
                isData = true,
                isPlay = autoplayController.state.value.screenState.isPlay,
            )
        }
    }

    fun sendEvent(event: EventStoryScreenEvents) {
        when (event) {
            is EventStoryScreenEvents.OnClickPlayButton -> {
                handlePlayState()
            }

            is EventStoryScreenEvents.OnClickNextItem -> {
                playNextStory()
            }

            is EventStoryScreenEvents.OnClickPreviousItem -> {
                playPreviousStory()
            }
        }
    }

    private fun handlePlayState() {
        timer.resetTimer()
        val isPlay = !state.value.isPlay
        stopTimer()
        mutableState.update { it.copy(isPlay = isPlay) }
        if (isPlay) timer.startTimer()
    }

    private fun playNextStory() {
        timer.resetTimer()
        if (isLastStory()) {
            onLastStory()
        } else {
            moveToNextStory()
        }
    }

    private fun playPreviousStory() {
        timer.resetTimer()
        if (isFirstStory()) {
            onFirstStory()
        } else {
            moveToPreviousStory()
        }
    }

    private fun moveToNextStory() =
        mutableState.update { it.copy(currentStoryIndex = it.currentStoryIndex + 1) }

    private fun moveToPreviousStory() =
        mutableState.update { it.copy(currentStoryIndex = it.currentStoryIndex - 1) }

    private fun isLastStory() = state.value.currentStoryIndex + 1 == state.value.eventsInfo.size

    private fun isFirstStory() = state.value.currentStoryIndex == 0

    private fun onLastStory() {
        autoplayController.nextScreen(state.value.toScreenState(true))
        mutableState.update { it.copy(currentStoryIndex = 0) }
    }

    private fun onFirstStory() {
        autoplayController.prevScreen(state.value.toScreenState(false))
        mutableState.update { it.copy(currentStoryIndex = state.value.eventsInfo.size - 1) }
    }

    fun startTimer() {
        timer.startTimer()
        //mutableState.update { it.copy(isPlay = true) }
    }

    fun stopTimer() {
        timer.stopTimer()
        //mutableState.update { it.copy(isPlay = false) }
    }

    private fun initTimer() {
        timer.init(
            scope = viewModelScope,
            callbackToEnd = {
                if (isLastStory()) {
                    onLastStory()
                } else {
                    moveToNextStory()
                }
            },
            isPlay = state.value.isPlay
        )
        viewModelScope.launch {
            timer.process.collect { progress -> mutableState.update { it.copy(storyProcess = progress) } }
        }
    }

    private fun BotMessage.toMessageInfo(): MessageInfo = MessageInfo(this)

    private val countUsersToShow = 10

    private val rowsInColumn = 5
    private fun LatestEventInfoUiState.toScreenState(direction: Boolean): ScreenState =
        ScreenState(isPlay = isPlay, isForwardDirection = direction)
}

