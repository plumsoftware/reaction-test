package presentation.aboutuser.viewmodel

import domain.model.dto.database.SessionDTO
import domain.model.either.AppEither
import domain.model.regular.user.User
import domain.storage.UserStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import presentation.aboutuser.store.Event
import presentation.aboutuser.store.Output
import presentation.aboutuser.store.State
import kotlin.coroutines.CoroutineContext

class AboutUserViewModel(
    private val userStorage: UserStorage,
    private val output: (Output) -> Unit,
    private val coroutineContextIO: CoroutineContext
) : ViewModel() {

    val state = MutableStateFlow(State())

    init {
        println("About user page created")
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.ChangeSelectedUser -> {
                viewModelScope.launch(coroutineContextIO) {
                    val sessions: List<SessionDTO> = userStorage.getSessions(userId = event.userId)
                    val user = userStorage.getAllUsers().find { it.id == event.userId }
                        ?: User.empty()
                    state.update {
                        it.copy(
                            user = user,
                            sessions = sessions,
                            filteredSessionsList = sessions,
                            login = user.login,
                            password = user.password
                        )
                    }
                }
            }

            Event.BackButtonClicked -> {
                clearState()
                onOutput(Output.BackButtonClicked)
            }

            Event.SaveChanges -> viewModelScope.launch(coroutineContextIO) { updateUser() }

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

            is Event.OnFilterChange -> {
                state.update {
                    it.copy(
                        testNumberFilter = event.filter
                    )
                }
            }

            Event.FilterSessions -> filterSessionsDTO()

            Event.DeleteUser -> viewModelScope.launch(coroutineContextIO) { deleteUser() }
            is Event.OnNameChanged -> {
                state.update {
                    it.copy(user = state.value.user.copy(name = event.name))
                }
            }

            is Event.OnPatronymicChanged -> {
                state.update {
                    it.copy(user = state.value.user.copy(patronymic = event.patronymic))
                }
            }

            is Event.OnSurnameChanged -> {
                state.update {
                    it.copy(user = state.value.user.copy(surname = event.surname))
                }
            }

            is Event.OnFilterChipClick -> {
                val list = state.value.selectedChipList
                list.set(index = event.index, element = event.selected)

                println("===${list}===")

                state.update {
                    it.copy(
                        selectedChipList = list
                    )
                }
            }
        }
    }

    private fun clearState() {
        state.update {
            State()
        }
    }

    private suspend fun updateUser() {
        if (state.value.login.isNotEmpty() && state.value.password.isNotEmpty() && state.value.user.name.isNotEmpty() && state.value.user.surname.isNotEmpty()) {
            state.update {
                it.copy(
                    isLoginError = false,
                    isPasswordError = false,
                    isNameError = false,
                    isSurnameError = false
                )
            }
            try {
                if (state.value.user.password != state.value.password) {
//                    region::Password change
                    val isPasswordUnique = userStorage.isPasswordUnique(state.value.password)
                    if (isPasswordUnique) {
                        val updatedUser: User = state.updateAndGet {
                            it.copy(
                                user = state.value.user.copy(
                                    login = state.value.login,
                                    password = state.value.password
                                )
                            )
                        }.user
                        userStorage.update(user = updatedUser)

                        state.update {
                            it.copy(appEither = AppEither.Success("Данные пользователя обновлены"))
                        }
                        delay(5000)
                        state.update {
                            it.copy(appEither = AppEither.Handle)
                        }
//                        endregion
                    } else {
                        state.update {
                            it.copy(
                                appEither = AppEither.Exception("Такой пароль уже существует"),
                                isPasswordError = true
                            )
                        }
                    }
                } else {
                    val updatedUser: User = state.updateAndGet {
                        it.copy(
                            user = state.value.user.copy(
                                login = state.value.login,
                            )
                        )
                    }.user
                    userStorage.update(user = updatedUser)

                    state.update {
                        it.copy(appEither = AppEither.Success("Данные пользователя обновлены"))
                    }
                    delay(5000)
                    state.update {
                        it.copy(appEither = AppEither.Handle)
                    }
                }
            } catch (e: Exception) {
                state.update {
                    it.copy(
                        appEither = AppEither.Exception("Ошибка: ${e.message}\nВызвана: ${e.cause.toString()}")
                    )
                }
            }
        } else {
            state.update {
                it.copy(
                    isLoginError = state.value.login.isEmpty(),
                    isPasswordError = state.value.password.isEmpty()
                )
            }
        }
    }

    private suspend fun deleteUser() {
        try {
            userStorage.delete(userId = state.value.user.id)
            state.update {
                it.copy(appEither = AppEither.Success("Пользователь удалён"))
            }
            delay(2000)
            state.update {
                it.copy(appEither = AppEither.Handle)
            }
            clearState()
            onOutput(Output.BackButtonClicked)
        } catch (e: Exception) {
            state.update {
                it.copy(appEither = AppEither.Exception("Ошибка: ${e.message}\nВызвана: ${e.cause.toString()}"))
            }
        }
    }

    private fun filterSessionsDTO() {
        if (state.value.testNumberFilter.isNotEmpty())
            try {
                val filter = state.value.testNumberFilter.toLong()

                if (filter > 0) {
                    val filteredSessionsDTOList = state.value.sessions.filter { it.testId == filter }
                    if (filteredSessionsDTOList.isNotEmpty())
                        state.update {
                            it.copy(filteredSessionsList = filteredSessionsDTOList, isFilterError = false)
                        }
                    else {
                        state.update {
                            it.copy(filteredSessionsList = emptyList(), isFilterError = false)
                        }
                    }
                } else {
                    state.update {
                        it.copy(filteredSessionsList = state.value.sessions, isFilterError = false)
                    }
                }
            } catch (e: Exception) {
                state.update {
                    it.copy(isFilterError = true)
                }
            }
        else
            state.update {
                it.copy(filteredSessionsList = state.value.sessions, isFilterError = false)
            }
    }

    private fun onOutput(o: Output) {
        output(o)
    }
}