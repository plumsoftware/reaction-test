package presentation.tests.traffic_light_test.store

import domain.model.dto.TestDTO
import domain.model.regular.user.Interval
import domain.model.regular.tests.TestMode
import domain.model.regular.user.User

data class State(
    val startTimerTime: Int = 10,
    val currentLampIndex: Int = -1,
    val start: Long = 0L,
    val end: Long = 0L,
    val user: User = User.empty(),
    val count: Int = 0,
    val userClicked: Int = 0,
    val signalInterval: Interval = Interval(),

    val intervals: MutableList<Long> = mutableListOf(),
    val errors: Int = 0,

    val testMode: TestMode? = TestMode.empty(),
    val testDTO: TestDTO = TestDTO()
)