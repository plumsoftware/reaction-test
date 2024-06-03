package presentation.users.store

import domain.model.regular.user.User

data class State (
    val list: List<User> = emptyList()
)