package presentation.authorization.login.viewmodel

import domain.model.dto.TestDTO
import domain.model.regular.DrivingLicenseCategory
import domain.model.regular.Gender
import domain.model.regular.Interval
import domain.model.regular.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import moe.tlaster.precompose.viewmodel.ViewModel
import presentation.authorization.login.store.Event
import presentation.authorization.login.store.Output
import presentation.authorization.login.store.State

class LoginViewModel(
    private val output: (Output) -> Unit
) : ViewModel() {

    val state = MutableStateFlow(State())

    init {
        println("Login view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackClicked -> onOutput(Output.BackButtonClicked)

            is Event.OnLoginChanged -> {
                state.update {
                    it.copy(
                        login = event.login
                    )
                }
            }

            is Event.OnPasswordChanged -> {
                state.update {
                    it.copy(
                        password = event.password
                    )
                }
            }

            Event.StartTest -> {
                if (
                    state.value.login.isEmpty() ||
                    state.value.password.isEmpty() ||
                    state.value.count == 0 ||
                    state.value.drivingLicenseCategory == DrivingLicenseCategory.Empty ||
                    state.value.experience < 0 ||
                    state.value.selectedInterval == Interval()
                ) {
                    state.update {
                        it.copy(
                            isLoginError = state.value.login.isEmpty(),
                            isPasswordError = state.value.password.isEmpty(),
                            isCountError = state.value.count == 0,
                            isDrivingLicenseCategoryError = state.value.drivingLicenseCategory == DrivingLicenseCategory.Empty,
                            isExperienceError = state.value.experience < 0,
                            isIntervalError = state.value.selectedInterval == Interval()
                        )
                    }
                } else {
                    /**
                    Go to the database with users and get a user data
                    MOCK DATA
                    MOCK DATA
                    MOCK DATA
                     **/
                    val user = User(
                        name = "Slava",
                        surname = "Deych",
                        patronymic = "Sergeevich",
                        age = 20,
                        gender = Gender.MALE,
                        drivingLicenseCategory = DrivingLicenseCategory.NoDrivingLicense
                    )
                    val testDto = TestDTO(
                        user = user,
                        count = state.value.count,
                        interval = state.value.selectedInterval
                    )
                    onOutput(Output.OpenTestMenu(testDTO = testDto))
                }
            }

            is Event.OnCountChanged -> {
                state.update {
                    it.copy(
                        count = event.count
                    )
                }
            }

            is Event.OnDrivingLicenseCategoryChanged -> {
                state.update {
                    it.copy(
                        drivingLicenseCategory = event.drivingLicenseCategory
                    )
                }
            }

            is Event.OnExperienceChanged -> {
                state.update {
                    it.copy(
                        experience = event.experience
                    )
                }
            }

            is Event.OnIntervalChanged -> {
                state.update {
                    it.copy(
                        selectedInterval = event.interval
                    )
                }
            }

            is Event.OnEnableStartTestChanged -> {
                state.update {
                    it.copy(
                        isEnableStartTest = event.enabled
                    )
                }
            }
            Event.OpenPrivacyPolicy -> {
                onOutput(Output.OpenPrivacyPolicy)
            }
        }
    }

    private fun onOutput(out: Output) {
        output(out)
    }
}