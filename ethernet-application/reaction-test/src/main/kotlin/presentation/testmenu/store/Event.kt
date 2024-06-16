package presentation.testmenu.store

import domain.model.regular.tests.TestMode
import domain.model.regular.user.User

sealed class Event {
    data class TestClicked(val route: String, val testMode: TestMode) : Event()

    data class ChangeUser(val user: User) : Event()
    data object BackCLicked : Event()
}