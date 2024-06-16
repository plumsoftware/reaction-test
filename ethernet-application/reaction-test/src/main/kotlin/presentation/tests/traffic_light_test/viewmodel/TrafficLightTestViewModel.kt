package presentation.tests.traffic_light_test.viewmodel

import domain.model.dto.TestDTO
import domain.model.dto.database.SessionDTO
import domain.storage.SessionStorage
import domain.storage.WorkbookStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import presentation.tests.traffic_light_test.store.Action
import presentation.tests.traffic_light_test.store.Event
import presentation.tests.traffic_light_test.store.Output
import presentation.tests.traffic_light_test.store.State
import java.util.Calendar
import kotlin.math.sqrt
import kotlin.random.Random

class TrafficLightTestViewModel(
    private val output: (Output) -> Unit,
    private val workbookStorage: WorkbookStorage,
    private val dataFormats: Map<String, Boolean>,
    private val localFolderToTable: String,
    private val actions: MutableStateFlow<Action?>,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    val state = MutableStateFlow(State())

    init {
        println("Traffic light Test ViewModel created")
        collectActions()
    }

    private fun collectActions() {
        viewModelScope.launch {
            actions.collect { action ->
                when (action) {
                    Action.StartTimer -> {
                        clearStartData()
                        while (state.value.startTimerTime > 0) {
                            delay(1000)
                            val temp = state.value.startTimerTime - 1
                            state.update {
                                it.copy(
                                    startTimerTime = temp
                                )
                            }
                        }
                        if (state.value.startTimerTime == 0) {
                            actions.emit(Action.GenerateRandomSignal)
                        }
                        println("Count is ${state.value.count}")
                    }

                    Action.GenerateRandomSignal -> {
                        generateRandomSignal()
                    }

                    is Action.IniStartData -> {
                        state.update {
                            it.copy(testDTO = action.testDTO)
                        }
                    }

                    null -> {}
                }
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackCLicked -> {
                onOutput(Output.BackButtonClicked)
            }

            is Event.OnTrafficLightLampButtonClicked -> {

                registerUserReactionTime()
                registerErrors(event.clickedLampIndex)

                if (state.value.userClicked != state.value.count)
                    generateRandomInterval()
                else {
                    stopTest()
                    viewModelScope.launch {
                        preRegisterDataDatabaseCompile()
                        registerDataInDatabase()
                    }
                    println("Test is finished")
                }
            }

            is Event.InitStartData -> state.update {
                with(event.testDTO.copy(testMode = state.value.testMode)) {
                    it.copy(
                        user = this.user,
                        count = this.count,
                        signalInterval = this.interval,
                        testMode = this.testMode,
                        testDTO = this
                    )
                }
            }

            is Event.InitTestMode -> {
                state.update {
                    it.copy(
                        testMode = event.testMode
                    )
                }
            }
        }
    }

    private fun onOutput(o: Output) {
        output(o)
    }

    private fun generateRandomSignal() {
        if (state.value.startTimerTime == 0) {
            val currentIndex = Random.nextInt(0, 3)

            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()

            state.update {
                it.copy(
                    currentLampIndex = currentIndex,
                    start = calendar.timeInMillis
                )
            }
            println("==================")
            println(
                "Signal was shown at ${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}:${
                    calendar.get(
                        Calendar.SECOND
                    )
                }"
            )
        }
    }

    private fun generateRandomInterval() {
        viewModelScope.launch {
            if (state.value.startTimerTime == 0 && state.value.currentLampIndex != -1) {
                state.update {
                    it.copy(
                        currentLampIndex = -1
                    )
                }

                val intervalSignal =
                    Random.nextLong(state.value.signalInterval.start, state.value.signalInterval.finish)

                println("==================")
                println("Signal interval is ${intervalSignal}msc")

                delay(intervalSignal)
                generateRandomSignal()
            }
        }
    }

    private fun registerErrors(clickedLampIndex: Int) {
        if (state.value.currentLampIndex != clickedLampIndex) {
            state.update {
                it.copy(errors = state.value.errors + 1)
            }
        }
    }

    private fun registerUserReactionTime() {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        val userInterval: Long = calendar.timeInMillis - state.value.start
        val userCalendar = Calendar.getInstance()
        userCalendar.timeInMillis = userInterval
        println("User reaction time is ${userCalendar.get(Calendar.SECOND)} seconds")

        state.value.intervals.add(userInterval)
        state.update {
            it.copy(
                end = calendar.timeInMillis,
                userClicked = state.value.userClicked + 1,
            )
        }

        println("==================")
        println(
            "Button was clicked at ${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}:${
                calendar.get(
                    Calendar.SECOND
                )
            }"
        )
    }

    private suspend fun preRegisterDataDatabaseCompile() {
        workbookStorage.createWorkbookIfNotExists(
            folderPath = localFolderToTable,
            dataFormats = dataFormats
        )
    }

    private suspend fun registerDataInDatabase() {
        val calendarForGetDate = Calendar.getInstance()
        val testDTO = TestDTO(
            user = state.value.user,
            count = state.value.count,
            interval = state.value.signalInterval,
            testMode = state.value.testMode,
            intervals = state.value.intervals.toList(),
            errorsCount = state.value.errors,
            sessionId = sessionStorage.getLastSessionIdUseCase(),
            averageValue = getAverage(),
            stdDeviationValue = getStdDeviation()
        )
        val sessionDTO = SessionDTO(
            sessionId = 0,
            userId = testDTO.user.id.toLong(),
            testId = testDTO.testMode?.id!!,
            testYear = calendarForGetDate.get(Calendar.YEAR),
            testMonth = calendarForGetDate.get(Calendar.MONTH),
            testDay = calendarForGetDate.get(Calendar.DAY_OF_MONTH),
            testHourOfDay24h = calendarForGetDate.get(Calendar.HOUR_OF_DAY),
            testMinuteOfHour = calendarForGetDate.get(Calendar.MINUTE),
            averageValue = getAverage(),
            standardDeviation = getStdDeviation(),
            count = testDTO.count,
            errors = testDTO.errorsCount!!,
            experience = testDTO.user.experience,
            userAge = testDTO.user.age,
            drivingLicenseCategory = testDTO.user.drivingLicenseCategory,
            signalInterval = testDTO.interval
        )
        workbookStorage.writeDataToWorkbook(
            testDTO = testDTO,
            folderPath = localFolderToTable,
            dataFormats = dataFormats
        )
        sessionStorage.insertOrAbort(
            sessionDTO = sessionDTO
        )
        sessionStorage.insertOrAbortNewSessionToRoamingDatabase(
            sessionDTO = sessionDTO
        )
    }

    private fun stopTest() {
        state.update {
            it.copy(
                currentLampIndex = -1
            )
        }
    }

    internal fun getAverage(): Double {
        val average = state.value.intervals.average()
        return average / 1000
    }

    internal fun getStdDeviation(): Double {
        val average = state.value.intervals.average()
        val variance = state.value.intervals.map { (it - average) * (it - average) }.average()
        return sqrt(variance)
    }

    private fun clearStartData() {
        state.update {
            it.copy(
                currentLampIndex = -1,
                intervals = mutableListOf(),
                startTimerTime = 10,
                userClicked = 0,
                errors = 0
            )
        }
    }
}